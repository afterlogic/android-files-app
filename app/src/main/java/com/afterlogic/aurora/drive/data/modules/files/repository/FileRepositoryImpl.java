package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.repository.AuthorizedRepository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FileRepositoryImpl extends AuthorizedRepository implements FilesRepository {

    private final static String FILES = "files";

    private final Context mAppContext;
    private final FileSubRepository mFileSubRepo;

    private final File mCacheDir;
    private final File mOfflineDir;

    @Inject
    FileRepositoryImpl(SharedObservableStore cache,
                              AuthRepository authRepository,
                              Context appContext,
                              FileSubRepository fileSubRepo,
                              @Named(FilesDataModule.CACHE_DIR) File cacheDir,
                              @Named(FilesDataModule.OFFLINE_DIR) File offlineDir) {
        super(cache, FILES, authRepository);
        mAppContext = appContext;
        mFileSubRepo = fileSubRepo;
        mCacheDir = cacheDir;
        mOfflineDir = offlineDir;
    }

    @Override
    public Single<List<String>> getAvailableFileTypes() {
        return mFileSubRepo.getAvailableFileTypes();
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return mFileSubRepo.getFiles(folder);
    }

    @Override
    public Single<Uri> getFileThumbnail(AuroraFile file) {
        return mFileSubRepo.getFileThumbnail(file);
    }

    @Override
    public Single<Uri> viewFile(AuroraFile file) {
        return mFileSubRepo.viewFile(file);
    }

    @Override
    public Completable createFolder(AuroraFile file) {
        return mFileSubRepo.createFolder(file);
    }

    @Override
    public final Single<AuroraFile> rename(AuroraFile file, String newName) {
        AuroraFile newFile = file.clone();
        newFile.setName(newName);
        return checkFileExisting(newFile)
                .andThen(Completable.error(new FileAlreadyExistError(newFile)))
                .compose(Observables.completeOnError(FileNotExistError.class))
                .andThen(mFileSubRepo.rename(file, newName))
                .andThen(checkFile(newFile));
    }

    @Override
    public Completable checkFileExisting(AuroraFile file) {
        return mFileSubRepo.checkFileExisting(file);
    }

    @Override
    public Single<AuroraFile> checkFile(AuroraFile file) {
        return mFileSubRepo.checkFile(file);
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

            return mFileSubRepo.delete(type, files);
        });
    }

    @Override
    public final Observable<Progressible<File>> download(AuroraFile file, File target) {
        return mFileSubRepo.checkFile(file)
                .flatMapObservable(checked -> {
                    File localActual = getOfflineOrCached(file);
                    if (localActual != null){
                        if (target.equals(localActual)){
                            return Observable.just(new Progressible<>(target, 0, 0, file.getName()));
                        } else {
                            return copyFile(file.getName(), localActual, target);
                        }
                    } else {
                        return downloadFile(file, target);
                    }
                });
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
                    .andThen(mFileSubRepo.uploadFileToServer(folder, fileInfo))
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

    @Override
    public Completable setOffline(AuroraFile file, boolean offline) {
        return null;
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

    private void checkFilesType(String type, List<AuroraFile> files) throws IllegalArgumentException{
        boolean allInType = Stream.of(files)
                .allMatch(file -> type.equals(file.getType()));
        if (!allInType){
            throw new IllegalArgumentException("All files must be in one type.");
        }
    }

    private Observable<Progressible<File>> copyFile(String progressName, File source, File target) throws IOException{
        return Observable.create(emitter -> {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(source);
                FileUtil.writeFile(fis, target, read -> emitter.onNext(
                        new Progressible<>(null, source.length(), read, progressName)
                ));
            } finally {
                IOUtil.closeQuietly(fis);
            }
            emitter.onNext(new Progressible<>(target, source.length(), source.length(), progressName));
            emitter.onComplete();
        });
    }

    private Observable<Progressible<File>> downloadFile(AuroraFile file, File target){
        Observable<Progressible<File>> request = mFileSubRepo.downloadFileBody(file)
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

    private File getOfflineOrCached(AuroraFile file){
        return Stream.of(mOfflineDir, mCacheDir)
                .map(dir -> FileUtil.getFile(dir, file))
                .filter(File::exists)
                .filter(local -> local.lastModified() == file.getLastModified())
                .findFirst()
                .orElse(null);
    }
}
