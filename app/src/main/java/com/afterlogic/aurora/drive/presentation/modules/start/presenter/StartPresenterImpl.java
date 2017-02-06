package com.afterlogic.aurora.drive.presentation.modules.start.presenter;

import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.start.interactor.StartInteractor;
import com.afterlogic.aurora.drive.presentation.modules.start.router.StartRouter;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartView;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StartPresenterImpl extends BasePresenter<StartView> implements StartPresenter {

    private final StartInteractor mInteractor;
    private final StartRouter mRouter;

    @Inject
    StartPresenterImpl(ViewState<StartView> viewState,
                               StartInteractor interactor,
                               StartRouter router) {
        super(viewState);
        mInteractor = interactor;
        mRouter = router;
    }

    @Override
    protected void onViewStart() {
        super.onViewStart();
        mInteractor.getAuthStatus()
                .subscribe(
                        this::handleAuthStatus,
                        this::onErrorObtained
                );
    }

    private void handleAuthStatus(boolean loggedIn){
        if (loggedIn){
            mRouter.openMain();
        } else {
            mRouter.openLogin();
        }
    }
}
