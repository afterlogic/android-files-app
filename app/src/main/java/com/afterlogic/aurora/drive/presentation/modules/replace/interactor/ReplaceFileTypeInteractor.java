package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.FolderStackSize;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeInteractor {

    private final FilesRepository filesRepository;
    private final ViewModelsConnection viewModelsConnection;

    @Inject
    public ReplaceFileTypeInteractor(FilesRepository filesRepository, ViewModelsConnection viewModelsConnection) {
        this.filesRepository = filesRepository;
        this.viewModelsConnection = viewModelsConnection;
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return filesRepository.getFiles(folder);
    }

    public Observable<String> listenPopRequest(String type) {
        return viewModelsConnection.popRequest
                .filter(it -> it.equals(type));
    }

    public void notifyStackChanged(String type, int depth) {
        viewModelsConnection.folderStackSize.onNext(new FolderStackSize(type, depth));
    }
}
