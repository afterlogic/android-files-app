package com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.router;

import android.content.Intent;

import com.afterlogic.aurora.drive.presentation.common.modules.model.router.BaseRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.AuroraActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.IntentUtil;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;

import javax.inject.Inject;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFileRouter<V extends PresentationView> extends BaseRouter<V, AuroraActivity> implements FilesRouter {

    @Inject
    public BaseFileRouter(ViewState<V> viewContext) {
        super(viewContext);
    }

    @Override
    public void goToOfflineError() {
        ifViewActive(activity -> {
            Intent offline = OfflineActivity.intent(activity,false);
            activity.startActivity(IntentUtil.makeRestartTask(offline));
            activity.overridePendingTransition(0, 0);
            activity.finish();
        });
    }

    @Override
    public void openAuth() {
        ifViewActive(activity -> {
            Intent login = LoginActivity.intent(false);
            activity.startActivity(IntentUtil.makeRestartTask(login));
            activity.finish();
        });
    }
}
