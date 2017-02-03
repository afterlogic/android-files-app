package com.afterlogic.aurora.drive.presentation.modules.filesMain.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesView;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesPresenterImpl extends BasePresenter<MainFilesView> implements MainFilesPresenter {

    @Inject MainFilesPresenterImpl(ViewState<MainFilesView> viewState) {
        super(viewState);
    }

    @Override
    protected void onViewStart() {
        super.onViewStart();
        getView().showMessage(
                "Runned!",
                PresentationView.TYPE_MESSAGE_MINOR
        );
    }
}
