package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import android.net.Uri;

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
    MainFilesListInteractor(FilesRepository filesRepository) {
        super(filesRepository);
        this.filesRepository = filesRepository;
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
}
