package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeInteractor {

    private final FilesRepository filesRepository;
    private final ReplaceFileTypeViewInteractor viewInteractor;

    @Inject
    ReplaceFileTypeInteractor(FilesRepository filesRepository, ReplaceFileTypeViewInteractor viewInteractor) {
        this.filesRepository = filesRepository;
        this.viewInteractor = viewInteractor;
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return filesRepository.getFiles(folder);
    }

    public Maybe<String> getCreateFolderName() {
        return viewInteractor.getFolderName();
    }

    public Single<AuroraFile> createFolder(String name, AuroraFile currentFolder) {
        AuroraFile newFolder = AuroraFile.create(currentFolder, name, true);
        return filesRepository.createFolder(newFolder)
                .andThen(Single.just(newFolder));
    }
}
