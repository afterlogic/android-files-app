package com.afterlogic.aurora.drive.data.modules.files.p8.repository;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive._unrefactored.model.project8.AuroraFileP8;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.BaseFilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.p8.service.FilesServiceP8;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesRepositoryP8Impl extends BaseFilesRepository implements FilesRepository {

    private static final String FILES_P_8 = "filesP8";

    private final FilesServiceP8 mFilesService;
    private final AppResources mAppResources;

    private final File mThumbDir;
    private final File mCacheDir;

    private final Mapper<AuroraFile, AuroraFileP8> mFileMapper = source -> new AuroraFile(
            source.getName(),
            source.getPath(),
            source.getFullPath(),
            source.isFolder(),
            source.isLink(),
            source.getLinkUrl(),
            0, //TODO convert link Type
            source.getThumbnailLink(),
            source.isThumb(),
            source.getContentType(),
            source.getHash(),
            source.getType(),
            source.getSize(),
            source.getLastModified() * 1000
    );

    private final Mapper<DeleteFileInfo, AuroraFile> mDeleteFileMapper = source -> new DeleteFileInfo(
            source.getPath(),
            source.getName()
    );

    @SuppressWarnings("WeakerAccess")
    @Inject
    FilesRepositoryP8Impl(SharedObservableStore cache,
                          FilesServiceP8 filesService,
                          AppResources appResources,
                          AuthRepository authRepository,
                          Context appContext,
                          @Named(FilesDataModule.THUMB_DIR) File thumbDir,
                          @Named(FilesDataModule.CACHE_DIR) File cacheDir) {
        super(cache, FILES_P_8, authRepository, appContext);
        mFilesService = filesService;
        mAppResources = appResources;
        mThumbDir = thumbDir;
        mCacheDir = cacheDir;
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
                    return files != null ? type : null;
                })
                .filter(ObjectsUtil::nonNull)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return withReloginNetMapper(
                () -> mFilesService.getFiles(folder.getType(), folder.getFullPath(), "")
                        .map(response -> response),
                result -> MapperUtil.list(mFileMapper).map(result.getFiles())
        );
    }

    @Override
    public final Single<Uri> getFileThumbnail(AuroraFile file) {
        Single<ResponseBody> thumbRequest = withNetMapper(
                mFilesService.getFileThumbnail(
                        file.getType(),
                        file.getPath(),
                        file.getName(),
                        file.getHash()
                ).map(response -> response),
                result -> ResponseBody.create(MediaType.parse("image/*"), Base64.decode(result, 0))
        );
        return loadFileToCache(file, mThumbDir, thumbRequest);
    }

    @Override
    public final Single<Uri> viewFile(AuroraFile file) {
        Single<ResponseBody> viewRequest = mFilesService.viewFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                file.getHash()
        );
        return loadFileToCache(file, mCacheDir, viewRequest);
    }

    @Override
    protected Completable renameByApi(AuroraFile file, String newName) {
        return withNetMapper(mFilesService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newName,
                file.isLink()
        ))//-----|
                .toCompletable();
    }

    @Override
    public Completable createFolder(AuroraFile file) {
        Single<ApiResponseP8<Boolean>> netRequest = mFilesService.createFolder(
                file.getType(),
                file.getPath(),
                file.getName()
        );
        return withNetMapper(netRequest).toCompletable();
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
    protected Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return mFilesService.downloadFile(file.getType(), file.getPath(), file.getName(), file.getHash());
    }

    @Override
    protected Observable<Progressible<UploadResult>> uploadFileToServer(AuroraFile folder, @NonNull FileInfo fileInfo) {
        SimpleObservableSource<Progressible<UploadResult>> progressSource = new SimpleObservableSource<>();

        Observable<Progressible<UploadResult>> request = withNetMapper(
                mFilesService.uploadFile(
                        folder.getType(),
                        folder.getFullPath(),
                        fileInfo,
                        (max, value) -> progressSource.onNext(new Progressible<>(
                                null, max, value, fileInfo.getName()
                        ))
                ).map(response -> response),
                result -> new UploadResult()
        )//-----|
                .doOnEvent((uploadResult, throwable) -> progressSource.complete())
                .doOnDispose(progressSource::clear)
                .map(result -> new Progressible<>(result, 0, 0, fileInfo.getName()))
                .toObservable()
                .doFinally(() -> MyLog.d("Request released."));

        return Observable.merge(
                progressSource,
                request
        );
    }

    @Override
    protected Completable deleteByApi(String type, List<AuroraFile> files) {
        return Completable.defer(() -> {
            List<DeleteFileInfo> deleteInfo = Stream.of(files)
                    .map(mDeleteFileMapper::map)
                    .collect(Collectors.toList());

            Single<ApiResponseP8<Boolean>> netRequest = mFilesService.delete(type, deleteInfo);
            return withNetMapper(netRequest)
                    .toCompletable();
        });
    }

    private Single<Uri> loadFileToCache(AuroraFile file, File cacheDir, Single<ResponseBody> cloud){
        return Single.defer(() -> {
            File cache = getCacheFile(cacheDir, file);
            if (cache.exists()){
                return Single.just(Uri.fromFile(cache));
            } else {
                return cloud.map(result -> saveIntoCache(result, file, cacheDir));
            }
        });
    }
}
