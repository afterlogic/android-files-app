package com.afterlogic.aurora.drive.presentation.modules.filesMain.router;

import android.content.Intent;

import com.afterlogic.aurora.drive.presentation.common.modules.router.BaseRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.IntentUtil;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesView;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginIntent;

import javax.inject.Inject;

/**
 * Created by sashka on 09.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesRouterImpl extends BaseRouter<MainFilesView, BaseActivity> implements MainFilesRouter {

    @Inject MainFilesRouterImpl(ViewState<MainFilesView> viewContext) {
        super(viewContext);
    }

    @Override
    public void openLogin() {
        ifViewActive(activity -> {
            Intent login = IntentUtil.makeRestartTask(LoginIntent.intent(activity));
            activity.startActivity(login);
        });
    }
}
