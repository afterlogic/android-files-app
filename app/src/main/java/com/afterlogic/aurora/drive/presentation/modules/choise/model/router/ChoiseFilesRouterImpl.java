package com.afterlogic.aurora.drive.presentation.modules.choise.model.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.BaseFileRouter;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesView;

import java.io.File;

import javax.inject.Inject;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseFilesRouterImpl extends BaseFileRouter<ChoiseFilesView> implements ChoiseFilesRouter {

    private final Context mAppContext;

    @Inject
    ChoiseFilesRouterImpl(ViewState<ChoiseFilesView> viewContext, Context appContext) {
        super(viewContext);
        mAppContext = appContext;
    }

    @Override
    public void closeWithResult(File result) {
        ifViewActive(activity -> {

            Uri fileUri = FileProvider.getUriForFile(
                    mAppContext, mAppContext.getPackageName() + ".fileProvider", result
            );
            Intent resultIntent = new Intent();
            resultIntent.setData(fileUri);
            resultIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);

            activity.setResult(RESULT_OK, resultIntent);
            activity.finish();
        });
    }

    @Override
    public void closeCurrent() {
        ifViewActive(activity -> {
            activity.setResult(RESULT_CANCELED);
            activity.finish();
        });
    }
}
