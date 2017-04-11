package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router;

import android.app.Activity;
import android.content.Intent;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.model.router.BaseMVVMRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.StoreableMVVMView;
import com.afterlogic.aurora.drive.presentation.common.util.IntentUtil;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginIntent;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;

import javax.inject.Inject;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFileMVVMRouter<V extends Activity & StoreableMVVMView> extends BaseMVVMRouter<V> implements FilesRouter {

    @Inject
    public BaseFileMVVMRouter(OptWeakRef<V> viewContext) {
        super(viewContext);
    }

    @Override
    public void goToOfflineError() {
        ifViewActive(activity -> {
            Intent offline = OfflineActivity.intent(false, activity);
            activity.startActivity(IntentUtil.makeRestartTask(offline));
            activity.overridePendingTransition(0, 0);
            activity.finish();
        });
    }

    @Override
    public void openAuth() {
        ifViewActive(activity -> {
            Intent offline = LoginIntent.intent(activity);
            activity.startActivity(IntentUtil.makeRestartTask(offline));
            activity.finish();
        });
    }
}
