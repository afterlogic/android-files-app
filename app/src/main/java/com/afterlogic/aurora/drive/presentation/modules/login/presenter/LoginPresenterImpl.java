package com.afterlogic.aurora.drive.presentation.modules.login.presenter;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.ErrorUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.error.AccountManagerError;
import com.afterlogic.aurora.drive.model.error.AuthError;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
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

    private final AppResources mAppResources;

    @Inject LoginPresenterImpl(ViewState<LoginView> viewState,
                               LoginInteractor loginInteractor,
                               LoginModel model,
                               LoginRouter router,
                               AppResources appResources) {
        super(viewState);
        mInteractor = loginInteractor;
        mModel = model;
        mRouter = router;
        mAppResources = appResources;
        model.setPresenter(this);
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
        switch (ErrorUtil.getErrorCode(throwable)){

            case AccountManagerError.CODE:
                getView().showMessage(
                        mAppResources.getString(R.string.error_adding_aurora_account),
                        PresentationView.TYPE_MESSAGE_MINOR
                );
                break;

            case AuthError.CODE:
                mModel.setPasswordError();
                break;

            case UnknownApiVersionError.CODE:
                mModel.setDomainError();
                break;

            default:
                onErrorObtained(throwable);
                getView().showMessage(
                        mAppResources.getString(R.string.error_connection_to_domain),
                        PresentationView.TYPE_MESSAGE_MINOR
                );
        }
    }
}
