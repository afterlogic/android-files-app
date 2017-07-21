package com.afterlogic.aurora.drive.presentation.modules.upload.v2.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.FilesListInteractor;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFilesInteractor extends FilesListInteractor {

    @Inject
    protected UploadFilesInteractor(FilesRepository filesRepository) {
        super(filesRepository);
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return super.getFiles(folder)
                .map(files -> Stream.of(files)
                        .filter(AuroraFile::isFolder)
                        .toList()
                );
    }
}
