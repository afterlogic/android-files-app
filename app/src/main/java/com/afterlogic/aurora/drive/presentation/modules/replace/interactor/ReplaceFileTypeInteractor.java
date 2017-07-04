package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeInteractor {

    private final FilesRepository filesRepository;

    @Inject
    public ReplaceFileTypeInteractor(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return filesRepository.getFiles(folder);
    }
}
