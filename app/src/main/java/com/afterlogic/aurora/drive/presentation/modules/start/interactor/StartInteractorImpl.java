package com.afterlogic.aurora.drive.presentation.modules.start.interactor;

import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StartInteractorImpl extends BaseInteractor implements StartInteractor {

    private final AccountHelper accountHelper;

    @Inject
    StartInteractorImpl(ObservableScheduler scheduler, AccountHelper accountHelper) {
        super(scheduler);
        this.accountHelper = accountHelper;
    }

    @Override
    public Single<Boolean> getAuthStatus() {
        return Single.fromCallable(accountHelper::hasCurrentAccount);
    }
}
