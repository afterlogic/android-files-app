package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.contextWrappers.ClipboardHelper;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.SearchableFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncListener;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.CACHE_DIR;
import static com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.DOWNLOADS_DIR;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListInteractor extends SearchableFilesListInteractor {

    private static int sWakeLockId = 0;

    private final FilesRepository filesRepository;
    private final SyncListener syncListener;
    private final ClipboardHelper clipboardHelper;

    private final Context appContext;
    private final File cacheDir;
    private final File downloadsDir;

    private final MainViewInteractor viewInteractor;

    private volatile int syncObservablesCount = 0;

    @Inject
    MainFilesListInteractor(FilesRepository filesRepository,
                            ClipboardHelper clipboardHelper,
                            Context appContext,
                            @Named(CACHE_DIR) File cacheDir,
                            @Named(DOWNLOADS_DIR) File downloadsDir,
                            MainViewInteractor viewInteractor) {
        super(filesRepository);
        this.filesRepository = filesRepository;
        this.clipboardHelper = clipboardHelper;
        this.syncListener = new SyncListener(appContext);
        this.appContext = appContext;
        this.cacheDir = cacheDir;
        this.downloadsDir = downloadsDir;
        this.viewInteractor = viewInteractor;
    }

    public Single<Uri> getThumbnail(AuroraFile file) {
        return filesRepository.getFileThumbnail(file);
    }

    public Single<Boolean> getOfflineStatus(AuroraFile file) {
        return filesRepository.getOfflineStatus(file);
    }

    public Observable<SyncProgress> getSyncProgress() {
        return syncListener.getProgressSource()
                .doOnSubscribe(disposable -> {
                    if (syncObservablesCount == 0) {
                        syncListener.onStart();
                    }
                    syncObservablesCount++;
                })
                .doFinally(() -> {
                    syncObservablesCount--;
                    if (syncObservablesCount == 0) {
                        syncListener.onStop();
                    }
                });
    }

    public Observable<Progressible<File>> downloadForOpen(AuroraFile file) {
        File target = FileUtil.getFile(cacheDir, file);
        return filesRepository.downloadOrGetOffline(file, target)
                .startWith(Observable.just(new Progressible<>(null, 0, 0, file.getName(), false)))
                .compose(this::prepareLoadTask);
    }

    public Maybe<String> getNewFolderName() {
        return viewInteractor.getNewFolderName();
    }

    public Single<AuroraFile> createNewFolder(String name, AuroraFile parentFolder) {
        return Single.defer(() -> {
            AuroraFile newFolder = AuroraFile.create(parentFolder, name, true);

            return filesRepository.createFolder(newFolder)
                    .andThen(Single.just(newFolder));
        });
    }

    public Single<Uri> getFileForUpload() {
        return viewInteractor.getFileForUpload();
    }

    public Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri file) {
        return filesRepository.uploadFile(folder, file)
                .compose(this::prepareLoadTask);
    }

    public Maybe<String> getNewFileName(AuroraFile file) {
        return viewInteractor.getNewNameForFile(file);
    }

    public Single<AuroraFile> rename(AuroraFile file, String newName) {
        return filesRepository.rename(file, newName);
    }

    public Completable deleteFile(AuroraFile file) {
        return filesRepository.delete(file);
    }

    public Observable<Progressible<File>> downloadToDownloads(AuroraFile file) {
        File target = FileUtil.getFile(downloadsDir, file);
        return filesRepository.downloadOrGetOffline(file, target)
                .compose(this::prepareLoadTask)
                .doOnNext(progress -> {
                    if (progress.isDone()){
                        DownloadManager dm = (DownloadManager) appContext.getSystemService(DOWNLOAD_SERVICE);
                        dm.addCompletedDownload(
                                target.getName(),
                                appContext.getString(R.string.prompt_downloaded_file_description),
                                true,
                                FileUtil.getFileMimeType(target),
                                target.getAbsolutePath(),
                                target.length(),
                                false
                        );
                    }
                });
    }

    public Completable setOffline(AuroraFile file, boolean offline) {
        return filesRepository.setOffline(file, offline)
                .andThen(Completable.fromAction(() -> {
                    // Start sync
                    if (offline) {
                        SyncService.requestSync(file, appContext);
                    }
                }));
    }

    public Completable createPublicLink(AuroraFile file) {
        return filesRepository.createPublicLink(file)
                .doOnSuccess(clipboardHelper::put)
                .toCompletable();
    }

    public Completable deletePublicLink(AuroraFile file) {
        return filesRepository.deletePublicLink(file);
    }

    private <T> Observable<T> prepareLoadTask(Observable<T> upstream) {
        return upstream.startWith(
                viewInteractor.requireWritePermission()
                        .observeOn(Schedulers.io())
                        .toObservable()
        )//-----|
                .compose(this::wakeLock);

    }

    private   <T> Observable<T> wakeLock(Observable<T> observable){
        Holder<PowerManager.WakeLock> wakeLockHolder = new Holder<>();
        return observable
                .doOnSubscribe(disposable -> {
                    PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "loadTask:" + sWakeLockId++);
                    wakeLockHolder.set(wakeLock);
                    wakeLock.acquire(TimeUnit.MINUTES.toMillis(30));
                })
                .doFinally(() -> wakeLockHolder.get().release());
    }
}
