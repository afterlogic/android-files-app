package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.interactor;

import android.content.Context;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileObserverInteractorImpl extends BaseInteractor implements FileObserverInteractor{

    private final File mOfflineDir;
    private final Context mAppContext;
    private final FilesRepository mFilesRepository;

    @Inject FileObserverInteractorImpl(ObservableScheduler scheduler,
                                       @Named(FilesDataModule.OFFLINE_DIR) File offlineDir,
                                       Context appContext,
                                       FilesRepository filesRepository) {
        super(scheduler);
        mOfflineDir = offlineDir;
        mAppContext = appContext;
        mFilesRepository = filesRepository;
    }

    @Override
    public Observable<File> observeOfflineFilesChanges() {
        return ChangedFilesObserver.observeFolder(mOfflineDir);
    }

    @Override
    public Maybe<AuroraFile> getOfflineFile(File file) {
        return Maybe.defer(() -> {
            String folderPath = mOfflineDir.getPath();
            String filePath = file.getPath();
            if (!filePath.startsWith(folderPath)){
                return Maybe.empty();
            } else {
                int separatorShift = folderPath.endsWith("/") ? 0 : + 1;
                String fileSpec = filePath.substring(folderPath.length() + separatorShift);
                return mFilesRepository.getOfflineFile(fileSpec);
            }
        });
    }

    @Override
    public Completable delete(File target) {
        return Completable.fromAction(() -> {
            if (target.exists() && !target.delete()){
                throw new IOException("Can't delete file");
            }
        });
    }

    @Override
    public Completable requestSync() {
        return Completable.fromAction(() -> SyncService.requestSync(mAppContext));
    }
}
