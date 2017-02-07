package com.afterlogic.aurora.drive.presentation.modules.filelist.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.afterlogic.aurora.drive._unrefactored.core.util.IntentUtil;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.FileViewActivity;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.router.BaseRouter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListView;

import java.util.List;

import javax.inject.Inject;

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
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(target.getLinkUrl()));

            if (IntentUtil.isAvailable(activity, intent)) {
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public boolean canOpenFile(AuroraFile target) {
        if (target.isPreviewAble()) return true;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (target.isLink()) {
            intent.setData(Uri.parse(target.getLinkUrl()));
        } else {
            intent.setType(target.getContentType());
        }

        return IntentUtil.isAvailable(mAppContext, intent);
    }
}
