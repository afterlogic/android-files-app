package com.afterlogic.aurora.drive.presentation.modules.offline.interactor;

import com.afterlogic.aurora.drive.data.common.multiapi.MultiApiService;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.FilesRootInteractor;

import org.jetbrains.annotations.NotNull;

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
    protected OfflineInteractor(MultiApiService<FilesRepository> filesRepository) {
        super(filesRepository);
    }

    @NotNull
    @Override
    public Single<List<Storage>> getAvailableFileTypes() {
        return Single.fromCallable(() -> Collections.singletonList(new Storage("offline", "Offline")));
    }

}
