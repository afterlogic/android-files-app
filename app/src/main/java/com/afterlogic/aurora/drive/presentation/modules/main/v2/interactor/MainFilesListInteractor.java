package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.SearchableFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFileActionRequest;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFilesActionsInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncListener;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListInteractor extends SearchableFilesListInteractor {

    private final FilesRepository filesRepository;
    private final SyncListener syncListener;

    private final MainFilesActionsInteractor filesActionsInteractor;

    private volatile int syncObservablesCount = 0;

    @Inject
    MainFilesListInteractor(FilesRepository filesRepository,
                            Context appContext,
                            MainFilesActionsInteractor filesActionsInteractor) {
        super(filesRepository);
        this.filesRepository = filesRepository;
        this.syncListener = new SyncListener(appContext);
        this.filesActionsInteractor = filesActionsInteractor;
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

    public void setFileForAction(MainFileActionRequest request) {
        filesActionsInteractor.setFileActionRequest(request);
    }
}
