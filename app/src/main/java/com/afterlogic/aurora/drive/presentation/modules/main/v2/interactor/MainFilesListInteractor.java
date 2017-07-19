package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.BaseFilesListInteractor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListInteractor extends BaseFilesListInteractor {

    private final FilesRepository filesRepository;

    @Inject
    protected MainFilesListInteractor(FilesRepository filesRepository) {
        super(filesRepository);
        this.filesRepository = filesRepository;
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder, String pattern) {
        return filesRepository.getFiles(folder, pattern);
    }
}
