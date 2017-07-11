package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.BaseFilesRootInteractor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceInteractor extends BaseFilesRootInteractor {

    private final FilesRepository filesRepository;

    @Inject
    ReplaceInteractor(FilesRepository filesRepository, AppResources appResources) {
        super(filesRepository, appResources);
        this.filesRepository = filesRepository;
    }

    public Completable replaceFiles(AuroraFile targetFolder, List<AuroraFile> source) {
        return filesRepository.replaceFiles(targetFolder, source);
    }

    public Completable copyFiles(AuroraFile targetFolder, List<AuroraFile> source) {
        return filesRepository.copyFiles(targetFolder, source);
    }
}
