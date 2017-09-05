package com.afterlogic.aurora.drive.presentation.modulesBackground.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.application.ActivityTracker;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by sunny on 01.09.17.
 */

public class NotificationActionReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.afterlogic.aurora.files.NOTIFICATION_ACTION";

    public static final String KEY_ACTION = "com.afterlogic.aurora.files.ACTION";

    private static final String ACTION_RELOGIN = "relogin";

    @Inject
    protected ActivityTracker activityTracker;

    public static Intent relogin() {
        return new Intent(ACTION)
                .putExtra(KEY_ACTION, ACTION_RELOGIN);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AndroidInjection.inject(this, context);

        switch (intent.getStringExtra(KEY_ACTION)) {

            case ACTION_RELOGIN:
                context.startActivity(LoginActivity.intent(true));
                break;

        }

    }
}
