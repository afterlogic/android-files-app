package com.afterlogic.aurora.drive.presentation.modules.offline.interactor;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.FilesRootInteractor;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineInteractor extends FilesRootInteractor {

    @Inject
    protected OfflineInteractor(FilesRepository filesRepository, AppResources appResources) {
        super(filesRepository, appResources);
    }

    @Override
    public Single<List<FileType>> getAvailableFileTypes() {
        return Single.fromCallable(() -> Collections.singletonList(new FileType("offline", "Offline")));
    }
}
