package com.afterlogic.aurora.drive.presentation.modules.main.model.router;

import android.content.Intent;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.util.IntentUtil;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.BaseFileMVVMRouter;
import com.afterlogic.aurora.drive.presentation.modules.about.view.AboutAppActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginIntent;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;

import javax.inject.Inject;

/**
 * Created by sashka on 09.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesRouterImpl extends BaseFileMVVMRouter<MainFilesActivity> implements MainFilesRouter {

    @Inject
    MainFilesRouterImpl(OptWeakRef<MainFilesActivity> viewContext) {
        super(viewContext);
    }

    @Override
    public void openLogin() {
        ifViewActive(activity -> {
            Intent login = IntentUtil.makeRestartTask(LoginIntent.intent(activity));
            activity.startActivity(login);
        });
    }

    @Override
    public void openOfflineMode() {
        ifViewActive(activity -> {
            Intent intent = OfflineActivity.intent(true, activity);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        });
    }

    @Override
    public void openAbout() {
        ifViewActive(activity -> {
            Intent intent = AboutAppActivity.intent(activity);
            activity.startActivity(intent);
        });
    }
}
