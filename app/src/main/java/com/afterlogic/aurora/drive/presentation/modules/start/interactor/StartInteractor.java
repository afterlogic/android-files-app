package com.afterlogic.aurora.drive.presentation.modules.start.interactor;

import com.afterlogic.aurora.drive.presentation.common.modules.interactor.Interactor;

import io.reactivex.Single;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface StartInteractor extends Interactor {
    Single<Boolean> getAuthStatus();
}
