package com.afterlogic.aurora.drive.application.navigation;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;

import com.afterlogic.aurora.drive.application.navigation.args.ExternalOpenFIleArgs;
import com.afterlogic.aurora.drive.application.navigation.args.ExternalShareFileArgs;
import com.afterlogic.aurora.drive.application.navigation.args.ExternalShareFilesArgs;
import com.afterlogic.aurora.drive.application.navigation.args.ReplaceScreenArgs;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.streams.ExtCollectors;
import com.afterlogic.aurora.drive.presentation.modules._util.BackToNullActivity;
import com.afterlogic.aurora.drive.presentation.modules.about.view.AboutAppActivity;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewActivity;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewArgs;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.view.MainFilesActionBottomSheet;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.view.OfflineActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceActivity;
import com.annimon.stream.Stream;

import java.util.ArrayList;

import ru.terrakok.cicerone.android.SupportAppNavigator;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static com.afterlogic.aurora.drive.R.string.prompt_send_by_email_chooser;

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
        try {
            
            if (command instanceof BackTo) {
                BackTo back = (BackTo) command;
                if (back.getScreenKey() == null) {
                    activity.startActivity(BackToNullActivity.restartTaskIntent(activity));
                    return;
                }
            }

            if (command instanceof ForwardWithResult) {
                ForwardWithResult forwardWithResult = (ForwardWithResult) command;

                Intent activityIntent = createActivityIntent(forwardWithResult.getScreenKey(), forwardWithResult.getTransitionData());

                // Start activity
                if (activityIntent != null) {
                    activity.startActivityForResult(activityIntent, forwardWithResult.getRequestCode());
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

            super.applyCommand(command);

        } catch (Throwable e) {

            if (command instanceof WithErrorHandling) {
                Consumer<Throwable> errorConsumer = ((WithErrorHandling) command).getErrorConsumer();
                if (errorConsumer != null) {
                    errorConsumer.consume(e);
                    return;
                }
            }

            throw e;
        }
    }

    @Override
    protected Intent createActivityIntent(String screenKey, Object data) {
        switch (screenKey) {

            case AppRouter.REPLACE: {
                ReplaceScreenArgs args = (ReplaceScreenArgs) data;
                return ReplaceActivity.newReplaceIntent(activity, args.getFiles());
            }

            case AppRouter.COPY: {
                ReplaceScreenArgs args = (ReplaceScreenArgs) data;
                return ReplaceActivity.newCopyIntent(activity, args.getFiles());
            }

            case AppRouter.LOGIN:
                return LoginActivity.intent(false, activity);

            case AppRouter.OFFLINE:
                return OfflineActivity.intent(activity, true);

            case AppRouter.ABOUT:
                return AboutAppActivity.intent(activity);

            case AppRouter.IMAGE_VIEW:
                FileViewArgs viewArgs = (FileViewArgs) data;
                return FileViewActivity.intent(viewArgs, activity);



            case AppRouter.EXTERNAL_BROWSER:
                return new Intent(ACTION_VIEW)
                        .setData(Uri.parse(data.toString()));

            case AppRouter.EXTERNAL_CHOOSE_FILE_FOR_UPLOAD:
                return new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE);

            case AppRouter.EXTERNAL_SHARE:
                if (data instanceof ExternalShareFileArgs) {

                    ExternalShareFileArgs args = (ExternalShareFileArgs) data;

                    Uri fileUri = FileProvider.getUriForFile(
                            activity, activity.getPackageName() + ".fileProvider", args.getLocal()
                    );

                    //Start 'send' intent, for attaching to email
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setType(args.getRemote().getContentType());
                    intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    return Intent.createChooser(intent, activity.getString(prompt_send_by_email_chooser));

                } else {

                    ExternalShareFilesArgs args = (ExternalShareFilesArgs) data;


                    ArrayList<Uri> fileUris = Stream.of(args.getFile())
                            .map(file -> FileProvider.getUriForFile(
                                    activity, activity.getPackageName() + ".fileProvider", file
                            ))
                            .collect(ExtCollectors.toArrayList());

                    //Start 'send' intent, for attaching to email
                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setType("*/*");
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    return Intent.createChooser(intent, activity.getString(prompt_send_by_email_chooser));
                }

            case AppRouter.EXTERNAL_OPEN_FILE:

                ExternalOpenFIleArgs openArgs = (ExternalOpenFIleArgs) data;

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
