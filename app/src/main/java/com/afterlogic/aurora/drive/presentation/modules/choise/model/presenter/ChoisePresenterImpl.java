package com.afterlogic.aurora.drive.presentation.modules.choise.model.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.presenter.BaseFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.router.FilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.choise.model.interactor.ChoiseInteractor;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseView;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseModel;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoisePresenterImpl extends BaseFilesPresenter<ChoiseView> implements ChoisePresenter {

    @Inject
    ChoisePresenterImpl(ViewState<ChoiseView> viewState, ChoiseInteractor interactor, ChoiseModel model, FilesRouter router) {
        super(viewState, interactor, model, router);
    }


}
