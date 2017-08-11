package com.afterlogic.aurora.drive.presentation.modules.upload.interactor;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.CurrentActivityTracker;

import javax.inject.Inject;

import io.reactivex.Maybe;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

class UploadFilesViewInteractor extends BaseViewInteractor {

    @Inject
    public UploadFilesViewInteractor(CurrentActivityTracker activityTracker) {
        super(activityTracker);
    }

    Maybe<String> getFolderName() {
        return getInputDialog(R.string.prompt_create_folder);
    }
}
