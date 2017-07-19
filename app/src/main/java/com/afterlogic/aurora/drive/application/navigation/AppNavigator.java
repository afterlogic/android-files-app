package com.afterlogic.aurora.drive.application.navigation;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._util.BackToNullActivity;
import com.afterlogic.aurora.drive.presentation.modules.about.view.AboutAppActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceActivity;

import java.util.List;

import ru.terrakok.cicerone.android.SupportAppNavigator;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class AppNavigator extends SupportAppNavigator {

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
    public void applyCommand(Command command) {
        if (command instanceof BackTo) {
            BackTo back = (BackTo) command;
            if (back.getScreenKey() == null) {
                activity.startActivity(BackToNullActivity.restartTaskIntent(activity));
                return;
            }
        }
        super.applyCommand(command);
    }

    @Override
    protected Intent createActivityIntent(String screenKey, Object data) {
        switch (screenKey) {

            case AppRouter.REPLACE:
                return ReplaceActivity.newReplaceIntent(activity, (List<AuroraFile>) data);

            case AppRouter.COPY:
                return ReplaceActivity.newCopyIntent(activity, (List<AuroraFile>) data);

            case AppRouter.LOGIN:
                return LoginActivity.intent(false, activity);

            case AppRouter.OFFLINE:
                return OfflineActivity.intent(true, activity);

            case AppRouter.ABOUT:
                return AboutAppActivity.intent(activity);

        }
        return null;
    }

    @Override
    protected Fragment createFragment(String screenKey, Object data) {
        return null;
    }
}
