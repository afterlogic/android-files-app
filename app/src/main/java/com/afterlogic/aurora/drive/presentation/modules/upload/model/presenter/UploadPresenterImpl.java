package com.afterlogic.aurora.drive.presentation.modules.upload.model.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.BaseFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.FilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.upload.model.interactor.UploadInteractor;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadView;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadModel;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadPresenterImpl extends BaseFilesPresenter<UploadView> implements UploadPresenter {

    private UploadModel mModel;

    @Inject UploadPresenterImpl(ViewState<UploadView> viewState, UploadInteractor interactor, UploadModel model, FilesRouter router) {
        super(viewState, interactor, model, router);
        mModel = model;
    }

    @Override
    protected void handleFilesError(Throwable error) {
        mModel.setErrorState(true);
        onErrorObtained(error);
    }
}
