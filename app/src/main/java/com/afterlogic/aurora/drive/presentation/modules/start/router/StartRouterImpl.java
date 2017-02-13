package com.afterlogic.aurora.drive.presentation.modules.start.router;

import com.afterlogic.aurora.drive.presentation.common.modules.router.BaseRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesIntent;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginIntent;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartView;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StartRouterImpl extends BaseRouter<StartView, BaseActivity> implements StartRouter {

    @Inject
    StartRouterImpl(ViewState<StartView> viewContext) {
        super(viewContext);
    }

    @Override
    public void openLogin() {
        ifViewActive(activity -> activity.startActivity(LoginIntent.intent(activity)));
    }

    @Override
    public void openMain() {
        ifViewActive(activity -> activity.startActivity(MainFilesIntent.intent(activity)));
    }
}
