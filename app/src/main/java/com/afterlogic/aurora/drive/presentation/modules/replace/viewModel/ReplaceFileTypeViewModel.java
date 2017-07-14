package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileListArgs;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceFileTypeInteractor;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeViewModel extends SearchableFileListViewModel<
        ReplaceFileTypeViewModel,
        ReplaceFileViewModel,
        FileListArgs
> {

    private final ReplaceFileTypeInteractor interactor;
    private final Subscriber subscriber;
    private final AppResources appResources;

    @Inject
    ReplaceFileTypeViewModel(ReplaceFileTypeInteractor interactor,
                             Subscriber subscriber,
                             AppResources appResources,
                             ViewModelsConnection<ReplaceFileTypeViewModel> viewModelsConnection) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.appResources = appResources;
    }

    @Override
    protected ReplaceFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return new ReplaceFileViewModel(file, onItemClickListener);
    }

    @Override
    protected void handleFiles(List<AuroraFile> files) {
        List<AuroraFile> folders = Stream.of(files)
                .filter(AuroraFile::isFolder)
                .toList();
        super.handleFiles(folders);
    }

    void onCreateFolder() {
        if (foldersStack.size() == 0) {
            return;
        }

        interactor.getCreateFolderName()
                .subscribe(subscriber.subscribe(this::createFolder));
    }

    private void createFolder(String name) {
        interactor.createFolder(name, foldersStack.get(0))
                .doOnSubscribe(disposable -> progress.set(ProgressViewModel.Factory.indeterminateCircle(
                        appResources.getString(R.string.prompt_dialog_title_folder_creation),
                        name
                )))
                .doFinally(() -> progress.set(null))
                .compose(subscriber::defaultSchedulers)
                .subscribe(this::onFolderCreated);
    }

    private void onFolderCreated(AuroraFile folder) {
        foldersStack.add(0, folder);
    }

}
