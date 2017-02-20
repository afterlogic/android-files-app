package com.afterlogic.aurora.drive.presentation.modules.choise.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.BaseFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.choise.interactor.ChoiseInteractor;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseView;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseModel;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoisePresenterImpl extends BaseFilesPresenter<ChoiseView> implements ChoisePresenter {

    @Inject
    ChoisePresenterImpl(ViewState<ChoiseView> viewState, ChoiseInteractor interactor, ChoiseModel model) {
        super(viewState, interactor, model);
    }


}
