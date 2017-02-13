package com.afterlogic.aurora.drive.data.modules.files;

import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.repository.AuthorizedRepository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public abstract class BaseFilesRepository extends AuthorizedRepository implements FilesRepository {

    private final Context mAppContext;

    @SuppressWarnings("WeakerAccess")
    public BaseFilesRepository(@RepositoryCache SharedObservableStore cache,
                                       String repositoryName,
                                       AuthRepository authRepository,
                                       Context appContext) {
        super(cache, repositoryName, authRepository);
        mAppContext = appContext;
    }

    protected abstract Completable deleteByApi(String type, List<AuroraFile> files);
    protected abstract Completable renameByApi(AuroraFile file, String newName);
    protected abstract Single<ResponseBody> downloadFileBody(AuroraFile file);
    protected abstract Observable<Progressible<UploadResult>> uploadFileToServer(AuroraFile folder, FileInfo fileInfo);

    @Override
    public final Single<AuroraFile> rename(AuroraFile file, String newName) {
        AuroraFile newFile = file.clone();
        newFile.setName(newName);
        return checkFileExisting(newFile)
                .andThen(Completable.error(new FileAlreadyExistError(newFile)))
                .compose(Observables.completeOnError(FileNotExistError.class))
                .andThen(renameByApi(file, newName))
                .andThen(checkFile(newFile));
    }

    @Override
    public final Completable delete(AuroraFile files) {
        return delete(Collections.singletonList(files));
    }

    @Override
    public final Completable delete(List<AuroraFile> files) {
        return Completable.defer(() -> {
            String type = files.get(0).getType();

            checkFilesType(type, files);

            return deleteByApi(type, files);
        });
    }

    @Override
    public final Observable<Progressible<File>> download(AuroraFile file, File target) {
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
    public final Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri file) {
        return Observable.defer(() -> {
            FileInfo fileInfo = FileUtil.fileInfo(file, mAppContext);

            //check file
            AuroraFile checkFile = AuroraFile.create(folder, fileInfo.getName(), false);
            return checkFileExisting(checkFile)
                    .andThen(Completable.error(new FileAlreadyExistError(checkFile)))
                    .compose(Observables.completeOnError(FileNotExistError.class))
                    //upload
                    .andThen(uploadFileToServer(folder, fileInfo))
                    .flatMap(progress -> {
                        if (!progress.isDone()){
                            return Observable.just(progress.map(null));
                        } else {
                            //TODO get uploaded file name
                            return checkFile(AuroraFile.create(folder, fileInfo.getName(), false))
                                    .map(progress::map)
                                    .toObservable()
                                    .startWith(new Progressible<>(null, -1, 0, progress.getName()));
                        }
                    });
        });
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

    protected Uri saveIntoCache(ResponseBody body, AuroraFile file, File cacheDir) throws IOException{
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

    protected File getCacheFile(File cacheDir, AuroraFile file){
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
