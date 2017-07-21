package com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.FilesRootInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.FilesRootViewModel;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadViewModel extends FilesRootViewModel<UploadFileListViewModel> {

    private final UploadViewModelsConnection viewModelsConnection;

    @Inject
    UploadViewModel(FilesRootInteractor interactor,
                              Subscriber subscriber,
                              Router router,
                              AppResources appResources,
                              UploadViewModelsConnection viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.viewModelsConnection = viewModelsConnection;
    }

    public void onCreateFolder() {
        viewModelsConnection.sendAction(getCurrentFileType(), UploadAction.CREATE_FOLDER);
    }
}
