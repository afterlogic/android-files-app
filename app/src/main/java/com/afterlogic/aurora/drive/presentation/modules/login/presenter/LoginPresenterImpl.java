package com.afterlogic.aurora.drive.presentation.modules.login.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractor;
import com.afterlogic.aurora.drive.presentation.modules.login.router.LoginRouter;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginView;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginModel;

import javax.inject.Inject;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginPresenterImpl extends BasePresenter<LoginView> implements LoginPresenter {

    private final LoginInteractor mInteractor;
    private final LoginModel mModel;
    private final LoginRouter mRouter;

    @Inject LoginPresenterImpl(ViewState<LoginView> viewState,
                               LoginInteractor loginInteractor,
                               LoginModel model,
                               LoginRouter router) {
        super(viewState);
        mInteractor = loginInteractor;
        mModel = model;
        mRouter = router;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        mInteractor.getCurrentSession()
                .subscribe(
                        mModel::setSessionData,
                        this::onErrorObtained
                );
    }

    @Override
    public void onLogin() {
        mModel.collectNewSessionData()
                .flatMapCompletable(
                        session -> mInteractor.login(session, mModel.isManualUrlScheme())
                )
                .doOnSubscribe(disposable -> mModel.setProgressState(true))
                .doFinally(() -> mModel.setProgressState(false))
                .subscribe(
                        mRouter::openNext,
                        this::handleLoginError
                );
    }

    private void handleLoginError(Throwable throwable){

    }
}
