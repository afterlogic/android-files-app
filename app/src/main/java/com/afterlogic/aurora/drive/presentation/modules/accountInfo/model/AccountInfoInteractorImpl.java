package com.afterlogic.aurora.drive.presentation.modules.accountInfo.model;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountInfoInteractorImpl implements AccountInfoInteractor{

    private final SessionManager sessionManager;

    @Inject AccountInfoInteractorImpl(SessionManager authRepository) {
        sessionManager = authRepository;
    }

    @Override
    public Single<String> getLogin() {

        return mapSession(AuroraSession::getEmail);

    }

    @Override
    public Single<String> getHost() {

        return mapSession(session -> session.getDomain().toString());

    }

    private <T> Single<T> mapSession(Mapper<T, AuroraSession> mapper) {

        return Single.fromCallable(() -> {

            AuroraSession session = sessionManager.getSession();

            if (session == null) {
                throw new IllegalStateException("Not authorized.");
            }

            return mapper.map(session);

        });

    }
}
