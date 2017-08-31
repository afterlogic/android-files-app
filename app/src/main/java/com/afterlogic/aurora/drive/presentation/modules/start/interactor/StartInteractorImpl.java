package com.afterlogic.aurora.drive.presentation.modules.start.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StartInteractorImpl extends BaseInteractor implements StartInteractor {

    private final SessionManager mSessionManager;

    @Inject
    StartInteractorImpl(ObservableScheduler scheduler, SessionManager sessionManager) {
        super(scheduler);
        mSessionManager = sessionManager;
    }

    @Override
    public Single<Boolean> getAuthStatus() {
        return Single.fromCallable(() -> {
            AuroraSession session = mSessionManager.getSession();
            return session != null;
        });
    }
}
