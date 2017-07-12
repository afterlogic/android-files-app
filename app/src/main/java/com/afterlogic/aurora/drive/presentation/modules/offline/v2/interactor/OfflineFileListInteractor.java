package com.afterlogic.aurora.drive.presentation.modules.offline.v2.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.SearchableFilesListInteractor;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileListInteractor extends SearchableFilesListInteractor {

    private final FilesRepository filesRepository;

    @Inject
    protected OfflineFileListInteractor(FilesRepository filesRepository) {
        super(filesRepository);
        this.filesRepository = filesRepository;
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return filesRepository.getOfflineFiles();
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder, String pattern) {

        String preparedPattern = pattern.trim().toLowerCase();

        return getFiles(folder)
                .map(files -> Stream.of(files)
                        .filter(file -> {
                            String preparedName = file.getName()
                                    .trim()
                                    .toLowerCase();
                            return preparedName.contains(preparedPattern);
                        })
                        .toList()
                );
    }
}
