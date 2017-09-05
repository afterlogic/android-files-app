package com.afterlogic.aurora.drive.presentation.modules._util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;

import com.afterlogic.aurora.drive.application.assembly.Injectable;
import com.afterlogic.aurora.drive.application.navigation.AppNavigator;

import javax.inject.Inject;

import ru.terrakok.cicerone.NavigatorHolder;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class BackToNullActivity extends FragmentActivity implements Injectable {

    public static Intent restartTaskIntent(Context context) {
        ComponentName cn = new ComponentName(context, BackToNullActivity.class);
        return IntentCompat.makeRestartActivityTask(cn);
    }

    @Inject
    protected NavigatorHolder navigatorHolder;

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        navigatorHolder.setNavigator(new AppNavigator(this, -1));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }
}
