package com.afterlogic.aurora.drive.presentation.modules.fileView.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewPresentationView;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewPresenterImpl extends BasePresenter<FileViewPresentationView> implements FileViewPresenter {

    @Inject FileViewPresenterImpl(ViewState<FileViewPresentationView> viewState) {
        super(viewState);
    }
}
