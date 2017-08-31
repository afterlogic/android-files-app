package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.AuthorizationResolver;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.network.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.model.AuroraFilesResponse;
import com.afterlogic.aurora.drive.data.model.UploadResult;
import com.afterlogic.aurora.drive.data.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive.data.model.project7.UploadResultP7;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.file.factory.AuroraFileP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.uploadResult.factory.UploadResultP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ReplaceFileDto;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesServiceP7;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

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
public class P7FileSubRepositoryImpl extends Repository implements FileSubRepository {

    private static final String FILES_P_7 = "filesP7";

    private final Mapper<AuroraFile, AuroraFileP7> mFileNetToBlMapper;
    private final Mapper<AuroraFileP7, AuroraFile> mFileBlToNetMapper;
    private final Mapper<UploadResult, UploadResultP7> mUploadResultToBlMapper;

    private final FilesServiceP7 mCloudService;
    private final DynamicDomainProvider mDynamicDomainProvider;
    private final SessionManager mSessionManager;
    private final AuthorizationResolver authorizationResolver;

    private final AppResources mAppResources;

    @SuppressWarnings("WeakerAccess")
    @Inject
    P7FileSubRepositoryImpl(SharedObservableStore cache,
                            AuroraFileP7MapperFactory mapperFactory,
                            FilesServiceP7 cloudService,
                            DynamicDomainProvider dynamicDomainProvider,
                            SessionManager sessionManager,
                            UploadResultP7MapperFactory uploadResultP7MapperFactory,
                            AppResources appResources,
                            AuthorizationResolver authorizationResolver) {
        super(cache, FILES_P_7);
        mFileNetToBlMapper = mapperFactory.netToBl();
        mFileBlToNetMapper = mapperFactory.blToNet();
        mCloudService = cloudService;
        mDynamicDomainProvider = dynamicDomainProvider;
        mSessionManager = sessionManager;
        mUploadResultToBlMapper = uploadResultP7MapperFactory.p7toBl();
        mAppResources = appResources;
        this.authorizationResolver = authorizationResolver;
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
                return mCloudService.getFiles(folder.getFullPath(), folder.getType(), pattern)
                        .compose(Repository::withNetMapper)
                        .compose(authorizationResolver::checkAuth)
                        .map(this::mapFilesResponse);
            }
        });
    }

    private List<AuroraFile> mapFilesResponse(AuroraFilesResponse files) {
        return MapperUtil.list(mFileNetToBlMapper).map(files.getFiles());
    }

    @Override
    public Completable rename(AuroraFile file, String newName) {
        return mCloudService.renameFile(
                file.getType(), file.getPath(), file.getName(), newName, file.isLink()
        )//-----|
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .toCompletable();
    }

    @Override
    public Observable<Progressible<UploadResult>> uploadFileToServer(AuroraFile folder, FileInfo fileInfo) {

        SimpleObservableSource<Progressible<UploadResult>> progressSource = new SimpleObservableSource<>();

        long size = fileInfo.getSize();
        Observable<Progressible<UploadResult>> request = mCloudService.upload(
                mFileBlToNetMapper.map(folder),
                fileInfo,
                (max, value) -> progressSource.onNext(new Progressible<>(
                        null, max, value, fileInfo.getName(), false
                ))
        )//-----|
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .doOnEvent((uploadResult, throwable) -> progressSource.complete())
                .doOnDispose(progressSource::clear)
                .map(result -> {
                    UploadResult mapped = mUploadResultToBlMapper.map(result);
                    return new Progressible<>(mapped, size, size, fileInfo.getName(), true);
                })
                .toObservable();

        return Observable.merge(
                progressSource,
                request
        );
    }

    @Override
    public Completable delete(String type, List<AuroraFile> files) {
        return Completable.defer(() -> {
            List<AuroraFileP7> mapped = MapperUtil.listOrEmpty(files, mFileBlToNetMapper);
            return mCloudService.deleteFiles(type, mapped)
                    .compose(Repository::withNetMapper)
                    .compose(authorizationResolver::checkAuth)
                    .toCompletable();
        });
    }

    @Override
    public Single<Uri> getFileThumbnail(AuroraFile file) {
        return Single.defer(() -> {
            Uri thumbUri;
            if (TextUtils.isEmpty(file.getThumbnailUrl())) {
                AuroraSession session = mSessionManager.getSession();
                String url = getCompleteUrl(
                        String.format(Locale.US, Api7.Links.THUMBNAIL_URL, session.getAccountId(), file.getHash(), session.getAuthToken())
                );
                thumbUri = Uri.parse(url);
            } else {
                thumbUri = Uri.parse(file.getThumbnailUrl());
            }
            return Single.just(thumbUri);
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
        return mCloudService.createFolder(file.getType(), file.getPath(), file.getName())
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .toCompletable();
    }


    @Override
    public Completable checkFileExisting(AuroraFile file) {
        return mCloudService.checkFile(file.getType(), file.getPath(), file.getName()).map(response -> response)
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .map(mFileNetToBlMapper::map)
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
    public Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return mCloudService.download(mFileBlToNetMapper.map(file));
    }

    @Override
    public Single<String> createPublicLink(AuroraFile file) {
        return mCloudService.createPublicLink(file.getType(), file.getPath(), file.getName(), file.getSize(), file.isFolder())
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth);
    }

    @Override
    public Completable deletePublicLink(AuroraFile file) {
        return mCloudService.deletePublicLink(file.getType(), file.getPath(), file.getName())
                .compose(Repository::withNetMapper)
                .compose(authorizationResolver::checkAuth)
                .toCompletable();
    }

    @Override
    public Completable replaceFiles(AuroraFile targetFolder, List<AuroraFile> files) {
        Mapper<List<ReplaceFileDto>, Collection<AuroraFile>> mapper = MapperUtil.listOrEmpty(new FileToReplaceFileMapper());

        return mCloudService.replaceFiles(
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

        return mCloudService.copyFiles(
                files.get(0).getType(),
                targetFolder.getType(),
                files.get(0).getPath(),
                targetFolder.getFullPath(),
                mapper.map(files)
        )//-----|
                .toCompletable();
    }

    private String getCompleteUrl(String url){
        return mDynamicDomainProvider.getDomain().toString() + url;
    }

}
