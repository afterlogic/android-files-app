package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.BaseFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncListener;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListInteractor extends BaseFilesListInteractor {

    private final FilesRepository filesRepository;
    private final SyncListener syncListener;

    private volatile int syncObservablesCount = 0;

    @Inject
    MainFilesListInteractor(FilesRepository filesRepository, Context appContext) {
        super(filesRepository);
        this.filesRepository = filesRepository;
        this.syncListener = new SyncListener(appContext);
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder, String pattern) {
        return filesRepository.getFiles(folder, pattern);
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
}
