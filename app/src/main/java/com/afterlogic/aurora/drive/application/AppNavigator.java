package com.afterlogic.aurora.drive.application;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceActivity;

import ru.terrakok.cicerone.android.SupportAppNavigator;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class AppNavigator extends SupportAppNavigator {

    public static final String REPLACE = "replace";

    private FragmentActivity activity;

    public AppNavigator(FragmentActivity activity, int containerId) {
        super(activity, containerId);
        this.activity = activity;
    }

    public AppNavigator(FragmentActivity activity, FragmentManager fragmentManager, int containerId) {
        super(activity, fragmentManager, containerId);
        this.activity = activity;
    }

    @Override
    protected Intent createActivityIntent(String screenKey, Object data) {
        switch (screenKey) {
            case REPLACE: {
                return ReplaceActivity.intent(activity);
            }
        }
        return null;
    }

    @Override
    protected Fragment createFragment(String screenKey, Object data) {
        return null;
    }
}
