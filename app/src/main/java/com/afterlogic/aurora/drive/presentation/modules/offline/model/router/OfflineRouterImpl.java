package com.afterlogic.aurora.drive.presentation.modules.offline.model.router;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.router.BaseMVVMRouter;
import com.afterlogic.aurora.drive.presentation.common.util.IntentUtil;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;

import java.io.File;

import javax.inject.Inject;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static com.afterlogic.aurora.drive.R.string.prompt_send_by_email_chooser;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineRouterImpl extends BaseMVVMRouter<OfflineActivity> implements OfflineRouter{

    private final App mAppContext;

    @Inject
    OfflineRouterImpl(OptWeakRef<OfflineActivity> viewContext, App appContext) {
        super(viewContext);
        mAppContext = appContext;
    }

    @Override
    public void openFile(AuroraFile remote, File file) {
        ifViewActive(activity -> {

            Uri fileUri = FileProvider.getUriForFile(
                    activity, activity.getPackageName() + ".fileProvider", file
            );

            //Open file in suitable application
            Intent intent = new Intent(ACTION_VIEW);
            intent.setDataAndType(fileUri, remote.getContentType());
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION | FLAG_GRANT_READ_URI_PERMISSION);

            activity.startActivity(intent);
        });
    }

    @Override
    public void openSendTo(AuroraFile source, File file) {
        ifViewActive(activity -> {
            Uri fileUri = FileProvider.getUriForFile(
                    activity, activity.getPackageName() + ".fileProvider", file
            );

            //Start 'send' intent, for attaching to email
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setType(source.getContentType());
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);

            activity.startActivity(
                    Intent.createChooser(intent, activity.getString(prompt_send_by_email_chooser))
            );
        });
    }

    @Override
    public void goToOnline() {
        ifViewActive(activity -> {
            Intent intent = MainFilesActivity.intent(activity);
            activity.startActivity(IntentUtil.makeRestartTask(intent));
        });
    }

    @Override
    public boolean canOpen(AuroraFile target) {
        Intent intent = new Intent(ACTION_VIEW);
        if (target.isLink()) {
            intent.setData(Uri.parse(target.getLinkUrl()));
        } else {
            intent.setType(target.getContentType());
        }
        return IntentUtil.isAvailable(mAppContext, intent);
    }
}
