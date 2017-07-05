package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.FolderStackSize;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceInteractor {

    private final FilesRepository filesRepository;
    private final ViewModelsConnection viewModelsConnection;

    private final Mapper<FileType, String> typesMapper;

    @Inject
    ReplaceInteractor(FilesRepository filesRepository, AppResources appResources, ViewModelsConnection viewModelsConnection) {
        this.filesRepository = filesRepository;
        typesMapper = new TypesMapper(appResources);
        this.viewModelsConnection = viewModelsConnection;
    }

    public Single<List<FileType>> getAvailableFileTypes() {
        return filesRepository.getAvailableFileTypes()
                .map(availableTypes -> MapperUtil.listOrEmpty(availableTypes, typesMapper));
    }

    public Observable<FolderStackSize> listenFoldersStack() {
        return viewModelsConnection.folderStackSize;
    }

    public Single<AuroraFile> getCurrentFolder(String type) {
        return Single.fromCallable(() -> viewModelsConnection.currentFolders.get(type));
    }

    public Completable replaceFiles(AuroraFile targetFolder, List<AuroraFile> source) {
        return filesRepository.replaceFiles(targetFolder, source);
    }

    public Completable copyFiles(AuroraFile targetFolder, List<AuroraFile> source) {
        return filesRepository.copyFiles(targetFolder, source);
    }

    public void popFolder(String type) {
        viewModelsConnection.popRequest.onNext(type);
    }

    public void notifyCreateFolder(String type) {
        viewModelsConnection.createFolderRequest.onNext(type);
    }
}
