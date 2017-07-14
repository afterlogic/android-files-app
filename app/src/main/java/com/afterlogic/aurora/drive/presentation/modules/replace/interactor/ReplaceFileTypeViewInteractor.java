package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;

import javax.inject.Inject;

import io.reactivex.Maybe;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

@SubModuleScope
public class ReplaceFileTypeViewInteractor extends BaseViewInteractor {

    @Inject
    ReplaceFileTypeViewInteractor() {
        super(null);
    }

    Maybe<String> getFolderName() {
        return getInputDialog(R.string.prompt_create_folder);
    }
}
