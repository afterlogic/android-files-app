package com.afterlogic.aurora.drive.presentation.modules.accountInfo.model;

import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountInfoInteractorImpl implements AccountInfoInteractor{

    private final AuthRepository mAuthRepository;

    @Inject AccountInfoInteractorImpl(AuthRepository authRepository) {
        mAuthRepository = authRepository;
    }

    @Override
    public Single<String> getLogin() {
        return mAuthRepository.getCurrentSession()
                .toSingle()
                .map(AuroraSession::getLogin);
    }

    @Override
    public Single<String> getHost() {
        return mAuthRepository.getCurrentSession()
                .toSingle()
                .map(session -> session.getDomain().toString());
    }
}
