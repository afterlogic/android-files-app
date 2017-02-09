package com.afterlogic.aurora.drive.presentation.modules.filelist.router;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.afterlogic.aurora.drive._unrefactored.core.util.IntentUtil;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.FileViewActivity;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.router.BaseRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListView;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static com.afterlogic.aurora.drive.R.string.prompt_send_by_email_chooser;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListRouterImpl extends BaseRouter<FileListView, BaseActivity> implements FileListRouter {

    private final Context mAppContext;

    @Inject
    FileListRouterImpl(ViewState<FileListView> viewContext, Context appContext) {
        super(viewContext);
        mAppContext = appContext;
    }

    @Override
    public void openImagePreview(AuroraFile target, List<AuroraFile> dirContent) {
        ifViewActive(activity -> activity.startActivity(
                FileViewActivity.IntentCreator.newInstance(dirContent, target, activity)
        ));
    }

    @Override
    public void openLink(AuroraFile target) {
        ifViewActive(activity -> {
            Intent intent = new Intent(ACTION_VIEW);
            intent.setData(Uri.parse(target.getLinkUrl()));

            if (IntentUtil.isAvailable(activity, intent)) {
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public boolean canOpenFile(AuroraFile target) {
        if (target.isPreviewAble()) return true;

        Intent intent = new Intent(ACTION_VIEW);
        if (target.isLink()) {
            intent.setData(Uri.parse(target.getLinkUrl()));
        } else {
            intent.setType(target.getContentType());
        }

        return IntentUtil.isAvailable(mAppContext, intent);
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType(source.getContentType());
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            activity.startActivity(
                    Intent.createChooser(intent, activity.getString(prompt_send_by_email_chooser))
            );
        });
    }

    @Override
    public void openUploadFileChooser() throws ActivityNotFoundException{
        ifViewActive(activity -> {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            activity.startActivityForResult(intent, FILE_SELECT_CODE);
        });
    }
}
