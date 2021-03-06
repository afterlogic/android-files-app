package com.afterlogic.aurora.drive.presentation.modules.upload.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.FilesListInteractor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFilesInteractor extends FilesListInteractor {

    private final FilesRepository filesRepository;
    private final UploadFilesViewInteractor viewInteractor;

    @Inject
    UploadFilesInteractor(FilesRepository filesRepository,
                          UploadFilesViewInteractor viewInteractor) {
        super(filesRepository);
        this.filesRepository = filesRepository;
        this.viewInteractor = viewInteractor;
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return super.getFiles(folder);
    }

    public Maybe<String> getCreateFolderName() {
        return viewInteractor.getFolderName();
    }

    public Single<AuroraFile> createFolder(String name, AuroraFile currentFolder) {
        AuroraFile newFolder = AuroraFile.create(currentFolder, name, true);
        return filesRepository.createFolder(newFolder)
                .andThen(Single.just(newFolder));
    }

    public Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri file) {
        return filesRepository.uploadFile(folder, file);
    }

    public Single<Uri> getThumbnail(AuroraFile file) {
        return filesRepository.getFileThumbnail(file);
    }

}
