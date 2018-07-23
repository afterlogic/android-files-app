package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.model.UploadResult;
import com.afterlogic.aurora.drive.data.model.project8.AuroraFileP8;
import com.afterlogic.aurora.drive.data.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.data.modules.AuthorizationResolver;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ReplaceFileDto;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesServiceP8;
import com.afterlogic.aurora.drive.model.Actions;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;


/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class P8FilesSubRepositoryImpl extends Repository implements FileSubRepository {

    private static final String FILES_P_8 = "filesP8";

    private final FilesServiceP8 filesService;
    private final AppResources appResources;

    private final File thumbDir;
    private final File cacheDir;

    private final AuthorizationResolver authorizationResolver;
    private final SessionManager sessionManager;

    private final Mapper<AuroraFile, AuroraFileP8> mapper = source -> {
        AuroraFile file = new AuroraFile(
                source.getName(),
                source.getPath(),
                source.getFullPath(),
                source.isFolder(),
                source.isLink(),
                source.getLinkUrl(),
                0, //TODO convert link Type
                source.getThumbnailUrl(),
                source.isThumb() || !TextUtils.isEmpty(source.getThumbnailUrl()),
                source.getContentType(),
                source.getHash(),
                source.getType(),
                source.getSize(),
                source.getLastModified() * 1000,
                source.isShared()
        );
        // Add actions
        if (source.getActions() != null) {
            Actions actions = new Actions(
                    source.getActions().getList() != null
            );
            file.setActions(actions);
        }
        return file;
    };

    private final Mapper<DeleteFileInfo, AuroraFile> deleteFileMapper = source -> new DeleteFileInfo(
            source.getPath(),
            source.getName()
    );

    @SuppressWarnings("WeakerAccess")
    @Inject
    P8FilesSubRepositoryImpl(SharedObservableStore cache,
                             FilesServiceP8 filesService,
                             AppResources appResources,
                             @Named(FilesDataModule.THUMB_DIR) File thumbDir,
                             @Named(FilesDataModule.CACHE_DIR) File cacheDir,
                             AuthorizationResolver authorizationResolver,
                             SessionManager sessionManager) {
        super(cache, FILES_P_8);
        this.authorizationResolver = authorizationResolver;
        this.filesService = filesService;
        this.appResources = appResources;
        this.thumbDir = thumbDir;
        this.cacheDir = cacheDir;
        this.sessionManager = sessionManager;
    }

    @Override
    public Single<List<Storage>> getAvailableStorages() {

        return filesService.getAvailableStorages()
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .map(dtos -> Stream.of(dtos)
                        .map(dto -> new Storage(dto.getType(), dto.getDisplayName()))
                        .toList()
                );

    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return getFiles(folder, null);
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder, @Nullable String pattern) {
        return filesService.getFiles(folder.getType(), folder.getFullPath(), pattern)
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .map(this::mapFilesResponse);
    }

    private List<AuroraFile> mapFilesResponse(FilesResponseP8 response) {
        return MapperUtil.list(mapper).map(response.getFiles());
    }

    @Override
    public final Single<Uri> getFileThumbnail(AuroraFile file) {
        return Single.defer(() -> {

            // TODO: add Authorization Bearer header to Glide and use thumbnail url every where

            if (file.isLink() && !TextUtils.isEmpty(file.getThumbnailUrl())) {
                String thumbUrl = file.getThumbnailUrl();
                if (thumbUrl.startsWith("?")) {
                    AuroraSession session = sessionManager.getSession();

                    if (session == null) {
                        throw new IllegalStateException("Not authorized.");
                    }

                    thumbUrl = session.getDomain().toString() + "/" + thumbUrl;
                }
                return Single.just(Uri.parse(thumbUrl.replace("\\\\/", "/")));
            } else {
                File cache = FileUtil.getFile(thumbDir, file);
                if (cache.exists() && cache.lastModified() == file.getLastModified()){
                    return Single.just(Uri.fromFile(cache));
                } else {

                    Single<ResponseBody> thumbRequest = filesService.getFileThumbnail(
                            file.getType(),
                            file.getPath(),
                            file.getName(),
                            file.getHash()
                    );
                    return loadFileToCache(file, thumbDir, thumbRequest);
                }
            }
        });
    }

    @Override
    public final Single<Uri> viewFile(AuroraFile file) {
        return Single.defer(() -> {
            File cache = FileUtil.getFile(cacheDir, file);
            if (cache.exists() && cache.length() > 0 && cache.lastModified() == file.getLastModified()){
                return Single.just(Uri.fromFile(cache));
            } else {
                Single<ResponseBody> viewRequest = filesService.viewFile(
                        file.getType(),
                        file.getPath(),
                        file.getName(),
                        file.getHash()
                );
                return loadFileToCache(file, cacheDir, viewRequest);
            }
        });
    }

    @Override
    public Completable rename(AuroraFile file, String newName) {
        return filesService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newName,
                file.isLink()
        ).compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .toCompletable();
    }

    @Override
    public Completable createFolder(AuroraFile file) {
        return filesService.createFolder(
                file.getType(),
                file.getPath(),
                file.getName()
        ).compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .toCompletable();
    }

    @Override
    public Completable checkFileExisting(AuroraFile file) {
        return checkFile(file).toCompletable();
    }

    @Override
    public Single<AuroraFile> checkFile(AuroraFile file) {
        return getFiles(file.getParentFolder())
                .flatMap(files -> Single.fromCallable(() ->
                        Stream.of(files)
                        .filter(remoteFile -> remoteFile.getFullPath().equals(file.getFullPath()))
                        .findFirst()
                        .orElseThrow(FileNotExistError::new))
                );
    }

    @Override
    public Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return filesService.downloadFile(file.getType(), file.getPath(), file.getName(), file.getHash());
    }

    @Override
    public Single<String> createPublicLink(AuroraFile file) {
        return filesService.createPublicLink(file.getType(), file.getPath(), file.getName(), file.getSize(), file.isFolder())
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth);
    }

    @Override
    public Completable deletePublicLink(AuroraFile file) {
        return filesService.deletePublicLink(file.getType(), file.getPath(), file.getName())
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .toCompletable();
    }

    @Override
    public Completable replaceFiles(AuroraFile targetFolder, List<AuroraFile> files) {
        Mapper<List<ReplaceFileDto>, Collection<AuroraFile>> mapper = MapperUtil.listOrEmpty(new FileToReplaceFileMapper());

        return filesService.replaceFiles(
                files.get(0).getType(),
                targetFolder.getType(),
                files.get(0).getPath(),
                targetFolder.getFullPath(),
                mapper.map(files)
        )//-----|
                .toCompletable();
    }

    @Override
    public Completable copyFiles(AuroraFile targetFolder, List<AuroraFile> files) {
        Mapper<List<ReplaceFileDto>, Collection<AuroraFile>> mapper = MapperUtil.listOrEmpty(new FileToReplaceFileMapper());

        return filesService.copyFiles(
                files.get(0).getType(),
                targetFolder.getType(),
                files.get(0).getPath(),
                targetFolder.getFullPath(),
                mapper.map(files)
        )//-----|
                .toCompletable();
    }

    @Override
    public Observable<Progressible<UploadResult>> uploadFileToServer(AuroraFile folder, @NonNull FileInfo fileInfo) {
        SimpleObservableSource<Progressible<UploadResult>> progressSource = new SimpleObservableSource<>();

        long size = fileInfo.getSize();
        Observable<Progressible<UploadResult>> request = filesService.uploadFile(
                        folder.getType(),
                        folder.getFullPath(),
                        fileInfo,
                        (max, value) -> progressSource.onNext(new Progressible<>(
                                null, max, value, fileInfo.getName(), false
                        ))
        )//-----|
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .map(response -> new UploadResult())
                .doOnEvent((uploadResult, throwable) -> progressSource.complete())
                .doOnDispose(progressSource::clear)
                .map(result -> new Progressible<>(result, size, size, fileInfo.getName(), true))
                .toObservable()
                .doFinally(() -> MyLog.d("Request released."));

        return Observable.merge(
                progressSource,
                request
        );
    }

    @Override
    public Completable delete(String type, List<AuroraFile> files) {
        return Completable.defer(() -> {
            List<DeleteFileInfo> deleteInfo = Stream.of(files)
                    .map(deleteFileMapper::map)
                    .collect(Collectors.toList());

            return filesService.delete(type, deleteInfo)
                    .compose(Repository::withNetMapper)
                    .toCompletable();
        });
    }

    private Single<Uri> loadFileToCache(AuroraFile file, File rootDir, Single<ResponseBody> cloud){
        return cloud.map(body -> {
            File cache = FileUtil.getFile(rootDir, file);
            InputStream is = body.byteStream();
            try {
                FileUtil.writeFile(body.byteStream(), cache);
                if (!cache.setLastModified(file.getLastModified())){
                    throw new IOException("Can't set last modified.");
                }
            } finally {
                IOUtil.closeQuietly(is);
            }
            return Uri.fromFile(cache);
        });
    }
}
