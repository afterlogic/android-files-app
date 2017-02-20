package com.afterlogic.aurora.drive.presentation.modules.login.interactor;

import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.Interactor;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface LoginInteractor extends Interactor {

    Maybe<AuroraSession> getCurrentSession();

    Completable login(AuroraSession session, boolean manualHost);
}
