package com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.FileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.interactor.UploadFilesInteractor;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFileListViewModel extends FileListViewModel<UploadFileListViewModel, UploadFileViewModel, UploadArgs> {

    private final UploadFilesInteractor interactor;

    @Inject
    public UploadFileListViewModel(UploadFilesInteractor interactor,
                                   Subscriber subscriber,
                                   ViewModelsConnection<UploadFileListViewModel> viewModelsConnection) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
    }

    @Override
    protected UploadFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return new UploadFileViewModel(file, onItemClickListener);
    }
}
