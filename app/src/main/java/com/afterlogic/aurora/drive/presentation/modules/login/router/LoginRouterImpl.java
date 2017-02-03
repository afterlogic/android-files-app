package com.afterlogic.aurora.drive.presentation.modules.login.router;

import com.afterlogic.aurora.drive.presentation.common.modules.router.BaseRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginView;

import javax.inject.Inject;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginRouterImpl extends BaseRouter<LoginView, BaseActivity> implements LoginRouter {

    @Inject LoginRouterImpl(ViewState<LoginView> viewContext) {
        super(viewContext);
    }

    @Override
    public void openNext() {

    }
}
