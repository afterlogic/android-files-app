package com.afterlogic.aurora.drive.application.navigation;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;

import com.afterlogic.aurora.drive.application.navigation.args.OpenExternalArgs;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._util.BackToNullActivity;
import com.afterlogic.aurora.drive.presentation.modules.about.view.AboutAppActivity;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewActivity;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewArgs;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.view.MainFilesActionBottomSheet;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.view.OfflineActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceActivity;

import java.util.List;

import ru.terrakok.cicerone.android.SupportAppNavigator;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

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
        if (command instanceof Forward) {
            Forward forward = (Forward) command;
            switch (forward.getScreenKey()) {
                case AppRouter.MAIN_FILE_ACTIONS:

                    MainFilesActionBottomSheet actionsFragment = MainFilesActionBottomSheet.newInstance();

                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction()
                            .addToBackStack(forward.getScreenKey());
                    actionsFragment.show(transaction, forward.getScreenKey());

                    return;
            }
        }
        try {
            super.applyCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                return OfflineActivity.intent(activity, true);

            case AppRouter.ABOUT:
                return AboutAppActivity.intent(activity);

            case AppRouter.EXTERNAL_BROWSER:
                return new Intent(ACTION_VIEW)
                        .setData(Uri.parse(data.toString()));

            case AppRouter.IMAGE_VIEW:
                FileViewArgs viewArgs = (FileViewArgs) data;
                return FileViewActivity.intent(viewArgs, activity);

            case AppRouter.EXTERNAL_OPEN_FILE:

                OpenExternalArgs openArgs = (OpenExternalArgs) data;

                Uri fileUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".fileProvider", openArgs.getLocal()
                );

                //Open file in suitable application
                return new Intent(ACTION_VIEW)
                        .setDataAndType(fileUri, openArgs.getRemote().getContentType())
                        .setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_GRANT_WRITE_URI_PERMISSION | FLAG_GRANT_READ_URI_PERMISSION);
        }
        return null;
    }

    @Override
    protected Fragment createFragment(String screenKey, Object data) {
        return null;
    }
}
