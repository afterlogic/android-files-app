package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.InteractorUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FilesRootInteractor {

    private final FilesRepository filesRepository;

    @Inject
    protected FilesRootInteractor(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public Single<List<Storage>> getAvailableFileTypes() {
        return filesRepository.getAvailableStorages()
                .compose(upstream -> InteractorUtil.retryIfNotAuthError(3, upstream));
    }

}
