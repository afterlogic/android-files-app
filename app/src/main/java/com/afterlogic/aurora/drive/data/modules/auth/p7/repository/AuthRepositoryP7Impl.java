package com.afterlogic.aurora.drive.data.modules.auth.p7.repository;

import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.auth.p7.service.AuthServiceP7;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.SystemAppData;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuthRepositoryP7Impl extends Repository implements AuthRepository {

    private static final String USER_P_7 = "userP7";

    private final AuthServiceP7 mAuthService;

    private final SessionManager mSessionManager;

    @Inject
    AuthRepositoryP7Impl(@RepositoryCache SharedObservableStore cache, AuthServiceP7 authService, SessionManager sessionManager) {
        super(cache, USER_P_7);
        mAuthService = authService;
        mSessionManager = sessionManager;
    }

    @Override
    public Completable login(String login, String password) {
        return withNetRawMapper(
                mAuthService.login(login, password)
                        .map(response -> response),
                response -> {
                    mSessionManager.getSession().setAccountId(response.getAccountId());
                    return response.getResult();
                }
        )//-----|
                .flatMapCompletable(authToken -> Completable.fromAction(() ->
                        mSessionManager.getSession().setAuthToken(authToken.token)
                ));
    }

    @Override
    public Completable relogin() {
        return Completable.defer(() -> {
            AuroraSession session = mSessionManager.getSession();
            return login(session.getLogin(), session.getPassword());
        });
    }

    @Override
    public Single<SystemAppData> getSystemAppData() {
        return withNetMapper(mAuthService.getSystemAppData())
                .map(result -> {
                    mSessionManager.getSession().setAppToken(result.getToken());
                    return result;
                });
    }
}
