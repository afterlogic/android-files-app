package com.afterlogic.aurora.drive.presentation.modules.filesMain.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.interactor.MainFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesView;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.MainFilesModel;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesPresenterImpl extends BasePresenter<MainFilesView> implements MainFilesPresenter {

    private final MainFilesInteractor mInteractor;
    private final MainFilesModel mModel;

    @Inject MainFilesPresenterImpl(ViewState<MainFilesView> viewState,
                                   MainFilesInteractor interactor,
                                   MainFilesModel model) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        mInteractor.getAvailableFileTypes()
                .subscribe(
                        mModel::setFileTypes,
                        this::onErrorObtained
                );
    }
}
