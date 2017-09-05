package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.InteractorUtil;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class SearchableFilesListInteractor extends FilesListInteractor {

    private final FilesRepository filesRepository;

    protected SearchableFilesListInteractor(FilesRepository filesRepository) {
        super(filesRepository);
        this.filesRepository = filesRepository;
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder, String pattern) {
        return filesRepository.getFiles(folder, pattern)
                .compose(upstream -> InteractorUtil.retryIfNotAuthError(3, upstream));
    }

}