package com.afterlogic.aurora.drive.presentation.modules.upload.interactor;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;
import com.afterlogic.aurora.drive.application.ActivityTracker;

import javax.inject.Inject;

import io.reactivex.Maybe;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

class UploadFilesViewInteractor extends BaseViewInteractor {

    @Inject
    UploadFilesViewInteractor(ActivityTracker activityTracker) {
        super(activityTracker);
    }

    Maybe<String> getFolderName() {
        return getInputDialog(R.string.prompt_create_folder);
    }
}
