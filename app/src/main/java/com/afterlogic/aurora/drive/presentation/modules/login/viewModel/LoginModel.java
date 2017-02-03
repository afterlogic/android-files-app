package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.modules.login.presenter.LoginPresenter;

import io.reactivex.Single;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface LoginModel {
    void setPresenter(LoginPresenter presenter);

    void setSessionData(AuroraSession sessionData);

    boolean isManualUrlScheme();

    Single<AuroraSession> collectNewSessionData();

    void setPasswordError();

    void setDomainError();

    void setProgressState(boolean inProgress);
}
