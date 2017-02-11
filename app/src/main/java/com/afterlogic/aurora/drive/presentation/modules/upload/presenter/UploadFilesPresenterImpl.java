package com.afterlogic.aurora.drive.presentation.modules.upload.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter.BaseFilesListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.interactor.UploadFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesView;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFilesModel;

import javax.inject.Inject;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesPresenterImpl extends BaseFilesListPresenter<UploadFilesView> implements UploadFilesPresenter {

    @Inject
    UploadFilesPresenterImpl(ViewState<UploadFilesView> viewState, UploadFilesInteractor interactor, UploadFilesModel model) {
        super(viewState, interactor, model);
    }
}
