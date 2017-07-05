package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.FolderStackSize;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeInteractor {

    private final FilesRepository filesRepository;
    private final ViewModelsConnection viewModelsConnection;
    private final ReplaceFileTypeViewInteractor viewInteractor;

    @Inject
    public ReplaceFileTypeInteractor(FilesRepository filesRepository, ViewModelsConnection viewModelsConnection, ReplaceFileTypeViewInteractor viewInteractor) {
        this.filesRepository = filesRepository;
        this.viewModelsConnection = viewModelsConnection;
        this.viewInteractor = viewInteractor;
    }

    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return filesRepository.getFiles(folder);
    }

    public Observable<String> listenPopRequest(String type) {
        return viewModelsConnection.popRequest
                .filter(it -> it.equals(type));
    }

    public Observable<String> listenCreateFolder(String type) {
        return viewModelsConnection.createFolderRequest
                .filter(it -> it.equals(type));
    }

    public void notifyStackChanged(String type, int depth) {
        viewModelsConnection.folderStackSize.onNext(new FolderStackSize(type, depth));
    }

    public void notifyCurrentFolderChanged(String type, AuroraFile currentFolder) {
        viewModelsConnection.currentFolders.put(type, currentFolder);
    }

    public Maybe<String> getCreateFolderName() {
        return viewInteractor.getFolderName();
    }

    public Completable createFolder(String name, AuroraFile currentFolder) {
        AuroraFile newFolder = AuroraFile.create(currentFolder, name, true);
        return filesRepository.createFolder(newFolder);
    }
}
