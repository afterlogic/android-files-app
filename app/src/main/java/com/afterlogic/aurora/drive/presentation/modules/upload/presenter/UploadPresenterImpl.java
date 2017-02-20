package com.afterlogic.aurora.drive.presentation.modules.upload.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.BaseFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.interactor.UploadInteractor;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadView;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadModel;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadPresenterImpl extends BaseFilesPresenter<UploadView> implements UploadPresenter {

    @Inject UploadPresenterImpl(ViewState<UploadView> viewState, UploadInteractor interactor, UploadModel model) {
        super(viewState, interactor, model);
    }
}
