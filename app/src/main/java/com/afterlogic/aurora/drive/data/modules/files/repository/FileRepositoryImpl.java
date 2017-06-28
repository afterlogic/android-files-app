package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.BiMapper;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.repository.AuthorizedRepository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.mapper.general.FilesMapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntity;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesLocalService;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.OfflineType;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.FileAlreadyExistError;
import com.afterlogic.aurora.drive.model.error.FileNotExistError;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FileRepositoryImpl extends AuthorizedRepository implements FilesRepository {

    private final static String FILES = "files";

    private final Context mAppContext;
    private final FileSubRepository mFileSubRepo;
    private final FilesLocalService mLocalService;

    private final AuthRepository mAuthRepository;

    private final File mCacheDir;
    private final File mOfflineDir;
    private final File mDownloadsDir;

    private final Mapper<AuroraFile, OfflineFileInfoEntity> mOfflineToBlMapper;

    @Inject
    FileRepositoryImpl(SharedObservableStore cache,
                       AuthRepository authRepository,
                       Context appContext,
                       FileSubRepository fileSubRepo,
                       FilesLocalService localService,
                       @Named(FilesDataModule.CACHE_DIR) File cacheDir,
                       @Named(FilesDataModule.OFFLINE_DIR) File offlineDir,
                       @Named(FilesDataModule.DOWNLOADS_DIR) File downloadsDir,
                       FilesMapperFactory mapperFactory) {
        super(cache, FILES, authRepository);
        mAuthRepository = authRepository;
        mAppContext = appContext;
        mFileSubRepo = fileSubRepo;
        mLocalService = localService;
        mCacheDir = cacheDir;
        mOfflineDir = offlineDir;
        mDownloadsDir = downloadsDir;

        BiMapper<AuroraFile, OfflineFileInfoEntity, File> offlineToBl = mapperFactory.offlineToBl();
        mOfflineToBlMapper = offlineFileInfo -> offlineToBl.map(offlineFileInfo, mOfflineDir);
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

            return mFileSubRepo.delete(type, files)
                    .andThen(Completable.defer(() -> Stream.of(files)
                            .map(it -> setOffline(it, false))
                            .collect(Observables.Collectors.concatCompletable())
                    ));
        });
    }

    @Override
    public final Observable<Progressible<File>> downloadOrGetOffline(AuroraFile file, File target) {
        return mFileSubRepo.checkFile(file)
                .onErrorResumeNext(error -> getOfflineFile(file.getPathSpec())
                        .flatMap(offlineFile -> offlineFile.getLastModified() != -1 ? Maybe.just(offlineFile) : Maybe.empty())
                        .switchIfEmpty(Maybe.error(error))
                        .toSingle()
                )
                .flatMapObservable(checked -> {
                    boolean isOffline = !mLocalService.get(file.getPathSpec()).isEmpty().blockingGet();
                    boolean toDownloads = target.getPath().startsWith(mDownloadsDir.getPath());

                    if (isOffline){
                        File offlineFile = new File(mOfflineDir, checked.getPathSpec());
                        if (offlineFile.exists() && offlineFile.lastModified() >= checked.getLastModified()){
                            return Observable.just(new Progressible<>(offlineFile, 0, 0, checked.getName(), true));
                        } else {
                            Observable<Progressible<File>> download = downloadFile(checked, target)
                                    .map(progress -> {
                                        if (toDownloads && progress.getProgress() > 0) {
                                            progress.setProgress((long) (progress.getProgress() * 0.9f));
                                        }
                                        return progress;
                                    });
                            Observable<Progressible<File>> copyFile = copyFile(checked.getName(), offlineFile, target)
                                    .map(progress -> {
                                        if (toDownloads && progress.getProgress() > 0){
                                            progress.setProgress((long) (progress.getMax() * 0.9f + progress.getProgress() * 0.1f));
                                        }
                                        return progress;
                                    });
                            return Observable.concat(
                                    download,
                                    toDownloads ? copyFile : Observable.empty()
                            );
                        }

                    } else {
                        File localActual = new File(mCacheDir, checked.getPathSpec());
                        if (localActual.exists() && localActual.lastModified() == checked.getLastModified()){
                            if (toDownloads){
                                return copyFile(checked.getName(), localActual, target);
                            } else {
                                return Observable.just(new Progressible<>(localActual, 0, 0, checked.getName(), true));
                            }
                        } else {
                            return downloadFile(checked, target);
                        }
                    }

                });
    }

    @Override
    public final Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri file) {
        return Observable.defer(() -> {
            FileInfo fileInfo = FileUtil.fileInfo(file, mAppContext);

            //check file
            //noinspection ConstantConditions
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
                            return checkFile(AuroraFile.create(folder, fileInfo.getName(), false))
                                    .map(progress::map)
                                    .toObservable()
                                    .startWith(new Progressible<>(null, progress.getMax(), progress.getProgress(), progress.getName(), false));
                        }
                    });
        });
    }

    @Override
    public Observable<Progressible<AuroraFile>> rewriteFile(AuroraFile file, Uri fileUri) {return Observable.defer(() -> {
        FileInfo fileInfo = FileUtil.fileInfo(fileUri, mAppContext);

        AuroraFile folder = file.getParentFolder();
        //check file and delete previous
        return checkFileExisting(file)
                .andThen(delete(file))
                .compose(Observables.completeOnError(FileNotExistError.class))
                //upload
                .andThen(mFileSubRepo.uploadFileToServer(folder, fileInfo))
                .flatMap(progress -> {
                    if (!progress.isDone()){
                        return Observable.just(progress.map(null));
                    } else {
                        //noinspection ConstantConditions
                        return checkFile(AuroraFile.create(folder, fileInfo.getName(), false))
                                .map(progress::map)
                                .toObservable()
                                .startWith(new Progressible<>(null, -1, 0, progress.getName(), false));
                    }
                });
    });
    }

    @Override
    public Completable setOffline(AuroraFile file, boolean offline) {
        return Completable.defer(() -> {
            if (offline){
                OfflineFileInfoEntity entity = new OfflineFileInfoEntity(file.getPathSpec(), OfflineType.OFFLINE.toString(), -1);
                return mLocalService.addOffline(entity)
                        .doOnComplete(() -> {
                            File cached = new File(mCacheDir, file.getPathSpec());
                            if (cached.exists() && !cached.delete()){
                                MyLog.majorException(new IOException("Can't delete file."));
                            }
                        });
            } else {
                return mLocalService.removeOffline(file.getPathSpec())
                        .doOnComplete(() -> {
                            File localFile = new File(mOfflineDir, file.getPathSpec());
                            if (localFile.exists() && !localFile.delete()){
                                MyLog.majorException(new IOException("Can't delete file."));
                            }
                        });
            }
        });
    }

    @Override
    public Maybe<AuroraFile> getOfflineFile(String pathSpec) {
        return mLocalService.get(pathSpec)
                .map(mOfflineToBlMapper::map);
    }

    @Override
    public Single<List<AuroraFile>> getOfflineFiles() {
        return mLocalService.getOffline()
                .map(offline -> MapperUtil.listOrEmpty(offline, mOfflineToBlMapper));
    }

    @Override
    public Single<Boolean> getOfflineStatus(AuroraFile file) {
        return mLocalService.get(file.getPathSpec())
                .isEmpty()
                .map(empty -> !empty);

    }

    @Override
    public Completable clearOfflineData() {
        return mLocalService.clear();
    }

    @Override
    public Single<String> createPublicLink(AuroraFile file) {
        return mFileSubRepo.createPublicLink(file)
                .map(link -> {
                    String domain = mAuthRepository.getCurrentSession()
                            .blockingGet()
                            .getDomain()
                            .toString();

                    String checkedLink;

                    if (link.startsWith("http://localhost")){
                        checkedLink = link.substring(16);
                    } else if (link.startsWith("https://localhost")){
                        checkedLink = link.substring(17);
                    } else {
                        checkedLink = link;
                    }

                    String resultLink;

                    if (checkedLink.startsWith("?")) {
                        resultLink = domain + checkedLink;
                    } else {
                        resultLink = checkedLink;
                    }
                    return resultLink;
                });
    }

    @Override
    public Completable deletePublicLink(AuroraFile file) {
        return mFileSubRepo.deletePublicLink(file);
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
                        new Progressible<>(null, source.length(), read, progressName, false)
                ));
            } finally {
                IOUtil.closeQuietly(fis);
            }
            emitter.onNext(new Progressible<>(target, source.length(), source.length(), progressName, true));
            emitter.onComplete();
        });
    }

    private Observable<Progressible<File>> downloadFile(AuroraFile file, File target){
        Observable<Progressible<File>> request = mFileSubRepo.downloadFileBody(file)
                .flatMapObservable(fileBody -> Observable.create(emitter -> {

                    long maxSize = file.getSize();

                    InputStream is = fileBody.byteStream();
                    try{
                        FileUtil.writeFile(is, target, maxSize,
                                written -> emitter.onNext(
                                        new Progressible<>(null, maxSize, written, file.getName(), false)
                                )
                        );
                    } finally {
                        IOUtil.closeQuietly(is);
                    }

                    if (!target.setLastModified(file.getLastModified())){
                        MyLog.majorException(new IOException("Can't set last modified: " + target.getPath()));
                    }

                    emitter.onNext(new Progressible<>(target, maxSize, maxSize, file.getName(), true));

                    emitter.onComplete();
                }));
        return Observable.concat(
                Observable.just(new Progressible<>(null, 0, 0, file.getName(), false)),
                request
        );
    }
}
