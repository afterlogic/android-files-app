package com.afterlogic.aurora.drive.presentation.modules.upload.model.router;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.BaseFileRouter;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesView;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesRouterImpl extends BaseFileRouter<UploadFilesView> implements UploadFilesRouter {

    @Inject
    UploadFilesRouterImpl(ViewState<UploadFilesView> viewContext) {
        super(viewContext);
    }

    @Override
    public void closeCurrent() {
        ifViewActive(activity -> {
            activity.setResult(RESULT_OK);
            activity.finish();
        });
    }
}