package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.ActivityResolver;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;

import javax.inject.Inject;

import io.reactivex.Maybe;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
public class ReplaceViewInteractor extends BaseViewInteractor {

    @Inject
    ReplaceViewInteractor(ActivityResolver activityResolver) {
        super(activityResolver);
    }

    Maybe<String> getFolderName() {
        return getInputDialog(R.string.prompt_create_folder);
    }
}
