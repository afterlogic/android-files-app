package com.afterlogic.aurora.drive.presentation.modules.fileView.interactor;

import android.app.DownloadManager;
import android.content.Context;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor.BaseFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.CACHE_DIR;
import static com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.DOWNLOADS_DIR;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewInteractorImpl extends BaseFilesListInteractor implements FileViewInteractor {

    private final FilesRepository mFilesRepository;
    private final Context mAppContext;
    private final File mCacheDir;
    private final File mDownloadsDir;

    @Inject
    FileViewInteractorImpl(ObservableScheduler scheduler,
                           FilesRepository filesRepository,
                           Context appContext,
                           @Named(CACHE_DIR) File cacheDir,
                           @Named(DOWNLOADS_DIR) File downloadsDir) {
        super(scheduler, filesRepository);
        mFilesRepository = filesRepository;
        mAppContext = appContext;
        mCacheDir = cacheDir;
        mDownloadsDir = downloadsDir;
    }

    @Override
    public Observable<Progressible<File>> downloadForOpen(AuroraFile file) {
        File target = FileUtil.getFile(mCacheDir, file);
        return mFilesRepository.downloadOrGetOffline(file, target)
                .compose(this::composeDefault);
    }

    @Override
    public Observable<Progressible<File>> downloadToDownloads(AuroraFile file) {
        File target = FileUtil.getFile(mDownloadsDir, file);
        return mFilesRepository.downloadOrGetOffline(file, target)
                .doOnNext(progress -> {
                    if (progress.isDone()){
                        DownloadManager dm = (DownloadManager) mAppContext.getSystemService(DOWNLOAD_SERVICE);
                        dm.addCompletedDownload(
                                target.getName(),
                                mAppContext.getString(R.string.prompt_downloaded_file_description),
                                true,
                                FileUtil.getFileMimeType(target),
                                target.getAbsolutePath(),
                                target.length(),
                                false
                        );
                    }
                })
                .compose(this::composeDefault);
    }

    @Override
    public Single<AuroraFile> rename(AuroraFile file, String newName) {
        return mFilesRepository.rename(file, newName)
                .compose(this::composeDefault);
    }

    @Override
    public Completable deleteFile(AuroraFile file) {
        return mFilesRepository.delete(file)
                .compose(this::composeDefault);
    }

    @Override
    public Completable setOffline(AuroraFile file, boolean offline) {
        return mFilesRepository.setOffline(file, offline)
                .andThen(Completable.fromAction(() -> {
                    if (offline) {
                        SyncService.requestSync(file, mAppContext);
                    }
                }))
                .compose(this::composeDefault);
    }

    @Override
    public Single<Boolean> getOfflineStatus(AuroraFile file) {
        return mFilesRepository.getOfflineStatus(file)
                .compose(this::composeDefault);
    }
}
