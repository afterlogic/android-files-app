package com.afterlogic.aurora.drive.presentation.modules.replace.interactor

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor
import com.afterlogic.aurora.drive.application.ActivityTracker

import javax.inject.Inject

import io.reactivex.Maybe

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
class ReplaceViewInteractor @Inject
internal constructor(tracker: ActivityTracker) : BaseViewInteractor(tracker) {

    val folderName: Maybe<String> = getInputDialog(R.string.prompt_create_folder)

}
