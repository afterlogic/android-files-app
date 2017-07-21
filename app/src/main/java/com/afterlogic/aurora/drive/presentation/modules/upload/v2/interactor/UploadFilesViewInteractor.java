package com.afterlogic.aurora.drive.presentation.modules.upload.v2.interactor;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.ActivityResolver;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;

import javax.inject.Inject;

import io.reactivex.Maybe;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

class UploadFilesViewInteractor extends BaseViewInteractor {

    @Inject
    public UploadFilesViewInteractor(ActivityResolver activityResolver) {
        super(activityResolver);
    }

    Maybe<String> getFolderName() {
        return getInputDialog(R.string.prompt_create_folder);
    }
}
