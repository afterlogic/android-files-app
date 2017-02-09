package com.afterlogic.aurora.drive.data.modules.files.p8.repository;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive._unrefactored.model.project8.AuroraFileP8;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.repository.AuthorizedRepository;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.p8.service.FilesServiceP8;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExist;
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

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesRepositoryP8Impl extends AuthorizedRepository implements FilesRepository {

    private static final String FILES_P_8 = "filesP8";

    private final FilesServiceP8 mFilesService;
    private final File mFilesCacheDir;
    private final File mThumbCacheDir;

    private final AppResources mAppResources;

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
    @Inject public FilesRepositoryP8Impl(@RepositoryCache SharedObservableStore cache,
                                         FilesServiceP8 filesService,
                                         Context context,
                                         AppResources appResources,
                                         AuthRepository authRepository) {
        super(cache, FILES_P_8, authRepository);
        mFilesService = filesService;
        mFilesCacheDir = FileUtil.getCacheFileDir(context);
        mThumbCacheDir = new File(context.getExternalCacheDir(), "thumb");
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
                () -> mFilesService.getFiles(folder.getType(), folder.getFullPath(), "")
                        .map(response -> response),
                result -> MapperUtil.list(mFileMapper).map(result.getFiles())
        );
    }

    @Override
    public Single<Uri> getFileThumbnail(AuroraFile file) {
        Single<ResponseBody> request = withNetMapper(
                mFilesService.getFileThumbnail(
                        file.getType(),
                        file.getPath(),
                        file.getName(),
                        file.getHash()
                ).map(response -> response),
                result -> ResponseBody.create(MediaType.parse("image/*"), Base64.decode(result, 0))
        );
        return getFile( file, mThumbCacheDir, request );
    }

    @Override
    public Single<Uri> viewFile(AuroraFile file) {
        Single<ResponseBody> request = mFilesService.viewFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                file.getHash()
        );
        return getFile(file, mFilesCacheDir, request);
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
    public Single<AuroraFile> rename(AuroraFile file, String newName) {
        AuroraFile newFile = file.clone();
        newFile.setName(newName);

        Completable renameRequest = withNetMapper(mFilesService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newFile.getName(),
                file.isLink()
        ))//-----|
                .toCompletable();

        return checkFile(newFile)
                .flatMap(remoteFile -> Single.error(new FileAlreadyExist()))
                .toCompletable()
                .onErrorResumeNext(error -> {
                    if (error instanceof FileNotExistError){
                        return Completable.complete();
                    } else {
                        return Completable.error(error);
                    }
                })
                .andThen(renameRequest)
                .andThen(checkFile(newFile));
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
    public Completable delete(AuroraFile files) {
        return delete(Collections.singletonList(files));
    }

    @Override
    public Completable delete(List<AuroraFile> files) {
        return Completable.defer(() -> {
            String type = files.get(0).getType();

            checkFilesType(type, files);

            List<DeleteFileInfo> deleteInfo = Stream.of(files)
                    .map(mDeleteFileMapper::map)
                    .collect(Collectors.toList());

            Single<ApiResponseP8<Boolean>> netRequest = mFilesService.delete(type, deleteInfo);
            return withNetMapper(netRequest)
                    .toCompletable();
        });
    }

    @Override
    public Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return mFilesService.downloadFile(file.getType(), file.getPath(), file.getName(), file.getHash());
    }

    @Override
    public Observable<Progressible<File>> download(AuroraFile file, File target) {
        Observable<Progressible<File>> request = downloadFileBody(file)
                .flatMapObservable(fileBody -> Observable.create(emitter -> {
                    File dir = target.getParentFile();

                    if (!dir.exists() && !dir.mkdirs()){
                        throw new IOException("Can't create dir: " + dir.toString());
                    }

                    long size = file.getSize();
                    saveFile(fileBody.byteStream(), file, target, emitter);

                    if (!target.setLastModified(file.getLastModified())){
                        MyLog.majorException(new IOException("Can't set last modified: " + target.getPath()));
                    }

                    emitter.onNext(new Progressible<>(target, size, size, file.getName()));
                    emitter.onComplete();
                }));
        return Observable.concat(
                Observable.just(new Progressible<>(null, 0, 0, file.getName())),
                request
        );
    }

    @Override
    public Single<UploadResult> uploadFile(AuroraFile folder, FileInfo file, @Nullable ApiTask.ProgressUpdater progressUpdater) {
        return withNetMapper(
                mFilesService.uploadFile(folder.getType(), folder.getFullPath(), file, progressUpdater)
                        .map(response -> response),
                result -> new UploadResult()
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

    private Single<Uri> getFile(AuroraFile file, File cacheDir, Single<ResponseBody> cloud){
        return Single.defer(() -> {
            File cache = getCacheFile(cacheDir, file);
            if (cache.exists()){
                return Single.just(Uri.fromFile(cache));
            } else {
                return cloud.map(result -> saveIntoCache(result, file, cacheDir));
            }
        });
    }

    private Uri saveIntoCache(ResponseBody body, AuroraFile file, File cacheDir) throws IOException{
        File target = getCacheFile(cacheDir, file);
        //noinspection ResultOfMethodCallIgnored
        target.delete();

        File dir = target.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Make dirs failed.");
        }

        FileOutputStream fos = new FileOutputStream(target);
        InputStream is = body.byteStream();

        int readed;
        byte[] buffer = new byte[2048];
        while ((readed = is.read(buffer)) != -1){
            fos.write(buffer, 0, readed);
        }

        fos.close();
        is.close();

        return Uri.fromFile(target);
    }

    private File getCacheFile(File cacheDir, AuroraFile file){
        return new File(cacheDir, file.getFullPath());
    }

    private void checkFilesType(String type, List<AuroraFile> files) throws IllegalArgumentException{
        boolean allInType = Stream.of(files)
                .allMatch(file -> type.equals(file.getType()));
        if (!allInType){
            throw new IllegalArgumentException("All files must be in one type.");
        }
    }
}
