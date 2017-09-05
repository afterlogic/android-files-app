package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.InteractorUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FilesListInteractor {

    private final FilesRepository filesRepository;

    @Inject
    protected FilesListInteractor(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return filesRepository.getFiles(folder)
                .compose(upstream -> InteractorUtil.retryIfNotAuthError(3, upstream));
    }

}
