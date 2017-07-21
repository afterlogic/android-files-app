package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.repository.AuthorizedRepository;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.model.UploadResult;
import com.afterlogic.aurora.drive.data.model.project8.AuroraFileP8;
import com.afterlogic.aurora.drive.data.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ReplaceFileDto;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesServiceP8;
import com.afterlogic.aurora.drive.model.Actions;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
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
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

import static com.afterlogic.aurora.drive.data.modules.files.repository.FileRepositoryUtil.CHECKED_TYPES;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class P8FilesSubRepositoryImpl extends AuthorizedRepository implements FileSubRepository {

    private static final String FILES_P_8 = "filesP8";

    private final FilesServiceP8 mFilesService;
    private final AppResources mAppResources;

    private final File mThumbDir;
    private final File mCacheDir;

    private final AuthRepository mAuthRepository;

    private final Mapper<AuroraFile, AuroraFileP8> mFileMapper = source -> {
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

    private final Mapper<DeleteFileInfo, AuroraFile> mDeleteFileMapper = source -> new DeleteFileInfo(
            source.getPath(),
            source.getName()
    );

    @SuppressWarnings("WeakerAccess")
    @Inject
    P8FilesSubRepositoryImpl(SharedObservableStore cache,
                             FilesServiceP8 filesService,
                             AppResources appResources,
                             AuthRepository authRepository,
                             @Named(FilesDataModule.THUMB_DIR) File thumbDir,
                             @Named(FilesDataModule.CACHE_DIR) File cacheDir) {
        super(cache, FILES_P_8, authRepository);
        mFilesService = filesService;
        mAppResources = appResources;
        mThumbDir = thumbDir;
        mCacheDir = cacheDir;
        mAuthRepository = authRepository;
    }

    @Override
    public Single<List<String>> getAvailableFileTypes() {
        return Single.fromCallable(() -> Stream.of(mAppResources.getStringArray(R.array.folder_types))
                .map(type -> {
                    List<AuroraFile> files = getFiles(AuroraFile.parse("", type, true))
                            .toMaybe()
                            .onErrorResumeNext(error -> {
                                if (error instanceof ApiResponseError){
                                    return Maybe.empty();
                                } else {
                                    return Maybe.error(error);
                                }
                            })
                            .blockingGet();
                    if (files != null) {
                        CHECKED_TYPES.put(type, files);
                        return type;
                    } else {
                        return null;
                    }
                })
                .filter(ObjectsUtil::nonNull)
                .collect(Collectors.toList())
        )//-----|
                .doFinally(FileRepositoryUtil::startClearCheckedCountDown);
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return getFiles(folder, null);
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder, @Nullable String pattern) {
        return Single.defer(() -> {
            if (TextUtils.isEmpty(pattern) && "".equals(folder.getFullPath()) && CHECKED_TYPES.containsKey(folder.getType())){
                List<AuroraFile> cached = CHECKED_TYPES.remove(folder.getType());
                return Single.just(cached);
            } else {
                return mFilesService.getFiles(folder.getType(), folder.getFullPath(), pattern)
                        .compose(Repository::withNetMapper)
                        .compose(this::withRelogin)
                        .map(this::mapFilesResponse);
            }
        });
    }

    private List<AuroraFile> mapFilesResponse(FilesResponseP8 response) {
        return MapperUtil.list(mFileMapper).map(response.getFiles());
    }

    @Override
    public final Single<Uri> getFileThumbnail(AuroraFile file) {
        return Single.defer(() -> {
            // TODO load by uri in glide
            if (!TextUtils.isEmpty(file.getThumbnailUrl()) && false) {
                String thumbUrl = file.getThumbnailUrl();
                if (thumbUrl.startsWith("?")) {
                    AuroraSession session = mAuthRepository.getCurrentSession().blockingGet();
                    thumbUrl = session.getDomain().toString() + "/" + thumbUrl;
                }
                return Single.just(Uri.parse(thumbUrl.replace("\\\\/", "/")));
            } else {
                File cache = FileUtil.getFile(mThumbDir, file);
                if (cache.exists() && cache.lastModified() == file.getLastModified()){
                    return Single.just(Uri.fromFile(cache));
                } else {

                    Single<ResponseBody> thumbRequest = mFilesService.getFileThumbnail(
                            file.getType(),
                            file.getPath(),
                            file.getName(),
                            file.getHash()
                    );
                    return loadFileToCache(file, mThumbDir, thumbRequest);
                }
            }
        });
    }

    @Override
    public final Single<Uri> viewFile(AuroraFile file) {
        return Single.defer(() -> {
            File cache = FileUtil.getFile(mCacheDir, file);
            if (cache.exists() && cache.length() > 0 && cache.lastModified() == file.getLastModified()){
                return Single.just(Uri.fromFile(cache));
            } else {
                Single<ResponseBody> viewRequest = mFilesService.viewFile(
                        file.getType(),
                        file.getPath(),
                        file.getName(),
                        file.getHash()
                );
                return loadFileToCache(file, mCacheDir, viewRequest);
            }
        });
    }

    @Override
    public Completable rename(AuroraFile file, String newName) {
        return mFilesService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newName,
                file.isLink()
        ).compose(Repository::withNetMapper)
                .compose(this::withRelogin)
                .toCompletable();
    }

    @Override
    public Completable createFolder(AuroraFile file) {
        return mFilesService.createFolder(
                file.getType(),
                file.getPath(),
                file.getName()
        ).compose(Repository::withNetMapper)
                .compose(this::withRelogin)
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
        return mFilesService.downloadFile(file.getType(), file.getPath(), file.getName(), file.getHash());
    }

    @Override
    public Single<String> createPublicLink(AuroraFile file) {
        return mFilesService.createPublicLink(file.getType(), file.getPath(), file.getName(), file.getSize(), file.isFolder())
                .compose(Repository::withNetMapper)
                .compose(this::withRelogin);
    }

    @Override
    public Completable deletePublicLink(AuroraFile file) {
        return mFilesService.deletePublicLink(file.getType(), file.getPath(), file.getName())
                .compose(Repository::withNetMapper)
                .compose(this::withRelogin)
                .toCompletable();
    }

    @Override
    public Completable replaceFiles(AuroraFile targetFolder, List<AuroraFile> files) {
        Mapper<List<ReplaceFileDto>, Collection<AuroraFile>> mapper = MapperUtil.listOrEmpty(new FileToReplaceFileMapper());

        return mFilesService.replaceFiles(
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

        return mFilesService.copyFiles(
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
        Observable<Progressible<UploadResult>> request = mFilesService.uploadFile(
                        folder.getType(),
                        folder.getFullPath(),
                        fileInfo,
                        (max, value) -> progressSource.onNext(new Progressible<>(
                                null, max, value, fileInfo.getName(), false
                        ))
        )//-----|
                .compose(Repository::withNetMapper)
                .compose(this::withRelogin)
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
                    .map(mDeleteFileMapper::map)
                    .collect(Collectors.toList());

            return mFilesService.delete(type, deleteInfo)
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
