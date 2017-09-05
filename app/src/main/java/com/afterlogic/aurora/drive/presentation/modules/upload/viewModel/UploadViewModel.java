package com.afterlogic.aurora.drive.presentation.modules.upload.viewModel;

import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.FilesRootInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.FilesRootViewModel;

import java.util.List;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadViewModel extends FilesRootViewModel<UploadFileListViewModel> {

    private final UploadViewModelsConnection viewModelsConnection;
    private final Router router;

    @Inject
    UploadViewModel(FilesRootInteractor interactor,
                    Subscriber subscriber,
                    Router router,
                    AppResources appResources,
                    UploadViewModelsConnection viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.viewModelsConnection = viewModelsConnection;
        this.router = router;
    }

    public void onCreateFolder() {
        viewModelsConnection.sendAction(getCurrentFileType(), UploadAction.CREATE_FOLDER);
    }

    public void setArgs(List<Uri> uploads) {
        if (uploads == null || uploads.isEmpty()) {
            router.exit();
        } else {
            viewModelsConnection.publishUploads(uploads);
        }
    }

    public void onUpload() {
        String currentFileType = getCurrentFileType();
        viewModelsConnection.sendAction(currentFileType, UploadAction.UPLOAD);
    }
}
