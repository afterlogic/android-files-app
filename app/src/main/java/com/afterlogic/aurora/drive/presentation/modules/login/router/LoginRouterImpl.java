package com.afterlogic.aurora.drive.presentation.modules.login.router;

import android.app.Activity;
import android.content.Intent;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.model.router.BaseMVVMRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.model.router.BaseRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginView;

import javax.inject.Inject;

import static com.afterlogic.aurora.drive.presentation.modules.login.view.LoginIntent.EXTRA_FINISH_ON_RESULT;
import static com.afterlogic.aurora.drive.presentation.modules.login.view.LoginIntent.EXTRA_NEXT_ACTIVITY;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginRouterImpl extends BaseMVVMRouter<LoginActivity> implements LoginRouter {

    @Inject
    LoginRouterImpl(OptWeakRef<LoginActivity> viewContext) {
        super(viewContext);
    }

    @Override
    public void openNext() {
        ifViewActive(activity -> {
            Intent requestIntent = activity.getIntent();
            if (!requestIntent.getBooleanExtra(EXTRA_FINISH_ON_RESULT, false)) {
                Class nextActivityClass = (Class) requestIntent.getSerializableExtra(EXTRA_NEXT_ACTIVITY);
                activity.startActivity(new Intent(activity, nextActivityClass));
            } else {
                activity.setResult(Activity.RESULT_OK);
            }
            activity.finish();
        });
    }
}
