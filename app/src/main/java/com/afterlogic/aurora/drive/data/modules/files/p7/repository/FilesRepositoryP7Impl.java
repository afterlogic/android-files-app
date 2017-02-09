package com.afterlogic.aurora.drive.data.modules.files.p7.repository;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.UploadResultP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.UploadedFileInfoP7;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.network.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.data.common.repository.AuthorizedRepository;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.file.factory.AuroraFileP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.uploadResult.factory.UploadResultP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.service.FilesServiceP7;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesRepositoryP7Impl extends AuthorizedRepository implements FilesRepository {

    private static final String FILES_P_7 = "filesP7";

    private final Mapper<AuroraFile, AuroraFileP7> mFileNetToBlMapper;
    private final Mapper<AuroraFileP7, AuroraFile> mFileBlToNetMapper;
    private final Mapper<UploadResult, UploadResultP7> mUploadResultToBlMapper;

    private final FilesServiceP7 mCloudService;
    private final DynamicDomainProvider mDynamicDomainProvider;
    private final SessionManager mSessionManager;

    private final AppResources mAppResources;
    private final Context mAppContext;

    @SuppressWarnings("WeakerAccess")
    @Inject public FilesRepositoryP7Impl(@RepositoryCache SharedObservableStore cache,
                                         AuroraFileP7MapperFactory mapperFactory,
                                         FilesServiceP7 cloudService,
                                         DynamicDomainProvider dynamicDomainProvider,
                                         SessionManager sessionManager,
                                         UploadResultP7MapperFactory uploadResultP7MapperFactory,
                                         AppResources appResources,
                                         AuthRepository authRepository,
                                         Context appContext) {
        super(cache, FILES_P_7, authRepository);
        mFileNetToBlMapper = mapperFactory.netToBl();
        mFileBlToNetMapper = mapperFactory.blToNet();
        mCloudService = cloudService;
        mDynamicDomainProvider = dynamicDomainProvider;
        mSessionManager = sessionManager;
        mUploadResultToBlMapper = uploadResultP7MapperFactory.p7toBl();
        mAppResources = appResources;
        mAppContext = appContext;
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
    public Single<AuroraFile> rename(AuroraFile file, String newName) {
        AuroraFile newFile = file.clone();
        newFile.setName(newName);

        Completable renameRequest = withNetMapper(mCloudService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newFile.getName(),
                file.isLink()
        )).toCompletable();

        return checkFileExisting(newFile)
                .andThen(Completable.error(new FileAlreadyExistError(newFile)))
                .compose(Observables.completeOnError(FileNotExistError.class))
                .andThen(renameRequest)
                .andThen(checkFile(newFile));
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
    public Completable delete(AuroraFile files) {
        return delete(Collections.singletonList(files));
    }

    @Override
    public Completable delete(List<AuroraFile> files) {
        return Completable.defer(() -> {
            String type = files.get(0).getType();
            checkFilesType(type, files);

            List<AuroraFileP7> mapped = MapperUtil.listOrEmpty(files, mFileBlToNetMapper);
            return withNetMapper(mCloudService.deleteFiles(type, mapped))
                    .toCompletable();
        });
    }

    @Override
    public Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return mCloudService.download(mFileBlToNetMapper.map(file));
    }

    @Override
    public Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri file) {
        return Observable.defer(() -> {
            FileInfo fileInfo = FileUtil.fileInfo(file, mAppContext);

            AuroraFile checkFile = AuroraFile.create(folder, fileInfo.getName(), false);
            //check
            return checkFileExisting(checkFile)
                    .andThen(Completable.error(new FileAlreadyExistError(checkFile)))
                    .compose(Observables.completeOnError(FileNotExistError.class))
                    //upload
                    .andThen(uploadFile(folder, fileInfo))
                    .flatMap(progress -> {
                        if (!progress.isDone()){
                            return Observable.just(progress.map(null));
                        } else {
                            UploadedFileInfoP7 uploadedFile = progress.getData();
                            return checkFile(AuroraFile.create(folder, uploadedFile.getName(), false))
                                    .map(progress::map)
                                    .toObservable()
                                    .startWith(new Progressible<>(null, -1, 0, progress.getName()));
                        }
                    });
        });
    }

    private Observable<Progressible<UploadedFileInfoP7>> uploadFile(AuroraFile folder, @NonNull FileInfo fileInfo){
        SimpleObservableSource<Progressible<UploadedFileInfoP7>> progressSource = new SimpleObservableSource<>();

        Observable<Progressible<UploadedFileInfoP7>> request = withNetMapper(
                mCloudService.upload(
                        mFileBlToNetMapper.map(folder),
                        fileInfo,
                        (max, value) -> progressSource.onNext(new Progressible<>(
                                null, max, value, fileInfo.getName()
                        ))
                ).map(response -> response),
                UploadResultP7::getFile
        )//-----|
                .doOnEvent((uploadResult, throwable) -> progressSource.complete())
                .doOnDispose(progressSource::clear)
                .map(result -> new Progressible<>(result, 0, 0, fileInfo.getName()))
                .toObservable();

        return Observable.merge(
                progressSource,
                request
        );
    }

    @Override
    public Observable<Progressible<File>> download(AuroraFile file, File target) {
        Observable<Progressible<File>> request = downloadFileBody(file)
                .flatMapObservable(fileBody -> Observable.create(emitter -> {
                    File dir = target.getParentFile();

                    if (!dir.exists() && !dir.mkdirs()){
                        throw new IOException("Can't create dir: " + dir.toString());
                    }

                    saveFile(fileBody.byteStream(), file, target, emitter);

                    if (!target.setLastModified(file.getLastModified())){
                        MyLog.majorException(new IOException("Can't set last modified: " + target.getPath()));
                    }

                    emitter.onNext(new Progressible<>(target, file.getSize(), file.getSize(), file.getName()));
                    emitter.onComplete();
                }));
        return Observable.concat(
                Observable.just(new Progressible<>(null, 0, 0, file.getName())),
                request
        );
    }

    /**
     * Read {@link ResponseBody} to local file.
     * @param is - resource input stream.
     * @param target - local file target.
     */
    private void saveFile(InputStream is, AuroraFile source, File target, ObservableEmitter<Progressible<File>> progressEmmiter) throws IOException{
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(target);
            progressEmmiter.onNext(new Progressible<>(null, source.getSize(), 0, source.getName()));

            byte[] buffer = new byte[2048];
            int count;
            long totalRead = 0;
            while ((count = is.read(buffer)) != -1 && totalRead < source.getSize()){
                count = (int) Math.min(source.getSize() - totalRead, count);
                fos.write(buffer, 0, count);
                totalRead += count;
                progressEmmiter.onNext(new Progressible<>(null, source.getSize(), totalRead, source.getName()));
            }
        } finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(fos);
        }
    }

    private String getCompleteUrl(String url){
        return mDynamicDomainProvider.getDomain().toString() + url;
    }

    private void checkFilesType(String type, List<AuroraFile> files) throws IllegalArgumentException{
        boolean allInType = Stream.of(files)
                .allMatch(file -> type.equals(file.getType()));
        if (!allInType){
            throw new IllegalArgumentException("All files must be in one type.");
        }
    }
}
