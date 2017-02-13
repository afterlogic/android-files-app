package com.afterlogic.aurora.drive.data.modules.files.p7.repository;

import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.UploadResultP7;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.network.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.BaseFilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.file.factory.AuroraFileP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.uploadResult.factory.UploadResultP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.service.FilesServiceP7;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesRepositoryP7Impl extends BaseFilesRepository implements FilesRepository {

    private static final String FILES_P_7 = "filesP7";

    private final Mapper<AuroraFile, AuroraFileP7> mFileNetToBlMapper;
    private final Mapper<AuroraFileP7, AuroraFile> mFileBlToNetMapper;
    private final Mapper<UploadResult, UploadResultP7> mUploadResultToBlMapper;

    private final FilesServiceP7 mCloudService;
    private final DynamicDomainProvider mDynamicDomainProvider;
    private final SessionManager mSessionManager;

    private final AppResources mAppResources;

    @SuppressWarnings("WeakerAccess")
    @Inject
    FilesRepositoryP7Impl(SharedObservableStore cache,
                          AuroraFileP7MapperFactory mapperFactory,
                          FilesServiceP7 cloudService,
                          DynamicDomainProvider dynamicDomainProvider,
                          SessionManager sessionManager,
                          UploadResultP7MapperFactory uploadResultP7MapperFactory,
                          AppResources appResources,
                          AuthRepository authRepository,
                          Context appContext) {
        super(cache, FILES_P_7, authRepository, appContext);
        mFileNetToBlMapper = mapperFactory.netToBl();
        mFileBlToNetMapper = mapperFactory.blToNet();
        mCloudService = cloudService;
        mDynamicDomainProvider = dynamicDomainProvider;
        mSessionManager = sessionManager;
        mUploadResultToBlMapper = uploadResultP7MapperFactory.p7toBl();
        mAppResources = appResources;
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
                () -> mCloudService.getFiles(folder.getFullPath(), folder.getType(), null)
                        .map(response -> response),
                files -> MapperUtil.list(mFileNetToBlMapper)
                        .map(files.getFiles())
        );
    }

    @Override
    protected Completable renameByApi(AuroraFile file, String newName) {
        return withNetMapper(mCloudService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newName,
                file.isLink()
        )).toCompletable();
    }

    @Override
    protected Observable<Progressible<UploadResult>> uploadFileToServer(AuroraFile folder, FileInfo fileInfo) {

        SimpleObservableSource<Progressible<UploadResult>> progressSource = new SimpleObservableSource<>();

        Observable<Progressible<UploadResult>> request = withNetMapper(
                mCloudService.upload(
                        mFileBlToNetMapper.map(folder),
                        fileInfo,
                        (max, value) -> progressSource.onNext(new Progressible<>(
                                null, max, value, fileInfo.getName()
                        ))
                ).map(response -> response)
        )//-----|
                .doOnEvent((uploadResult, throwable) -> progressSource.complete())
                .doOnDispose(progressSource::clear)
                .map(result -> {
                    UploadResult mapped = mUploadResultToBlMapper.map(result);
                    return new Progressible<>(mapped, 0, 0, fileInfo.getName());
                })
                .toObservable();

        return Observable.merge(
                progressSource,
                request
        );
    }

    @Override
    protected Completable deleteByApi(String type, List<AuroraFile> files) {
        return Completable.defer(() -> {
            List<AuroraFileP7> mapped = MapperUtil.listOrEmpty(files, mFileBlToNetMapper);
            return withNetMapper(mCloudService.deleteFiles(type, mapped))
                    .toCompletable();
        });
    }

    @Override
    public Single<Uri> getFileThumbnail(AuroraFile file) {
        return Single.defer(() -> {
            AuroraSession session = mSessionManager.getSession();
            String url = getCompleteUrl(
                    String.format(Locale.US, Api7.Links.THUMBNAIL_URL, session.getAccountId(), file.getHash(), session.getAuthToken())
            );
            return Single.just(Uri.parse(url));
        });
    }

    @Override
    public Single<Uri> viewFile(AuroraFile file) {
        return Single.defer(() -> {
            AuroraSession session = mSessionManager.getSession();
            String url = getCompleteUrl(
                    String.format(Locale.US, Api7.Links.FILE_DOWNLOAD_LINK, session.getAccountId(), file.getHash(), session.getAuthToken())
            );
            return Single.just(Uri.parse(url));
        });
    }

    @Override
    public Completable createFolder(AuroraFile file) {
        Single<ApiResponseP7<Boolean>> request = mCloudService.createFolder(
                file.getType(), file.getPath(), file.getName()
        );
        return withNetMapper(request).toCompletable();
    }


    @Override
    public Completable checkFileExisting(AuroraFile file) {
        return withNetMapper(
                mCloudService.checkFile(file.getType(), file.getPath(), file.getName()).map(response -> response),
                mFileNetToBlMapper
        )//-----|
                .toCompletable()
                .onErrorResumeNext(error -> {
                    if (error instanceof ApiResponseError && ((ApiResponseError) error).getErrorCode() == ApiResponseError.FILE_NOT_EXIST){
                        return Completable.error(FileNotExistError::new);
                    } else {
                        return Completable.error(error);
                    }
                });
    }

    @Override
    public Single<AuroraFile> checkFile(AuroraFile file) {
        return checkFileExisting(file)
                .andThen(getFiles(file.getParentFolder()))
                .map(files -> Stream.of(files)
                        .filter(fileItem -> fileItem.getFullPath().equals(file.getFullPath()))
                        .findFirst()
                        .orElseThrow(FileNotExistError::new)
                );
    }

    @Override
    protected Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return mCloudService.download(mFileBlToNetMapper.map(file));
    }

    private String getCompleteUrl(String url){
        return mDynamicDomainProvider.getDomain().toString() + url;
    }

}
