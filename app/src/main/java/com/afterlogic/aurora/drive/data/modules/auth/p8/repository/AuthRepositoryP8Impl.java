package com.afterlogic.aurora.drive.data.modules.auth.p8.repository;

import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.auth.p8.service.AuthServiceP8;
import com.afterlogic.aurora.drive._unrefactored.model.ApiResponse;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SuppressWarnings("WeakerAccess")
public class AuthRepositoryP8Impl extends Repository implements AuthRepository {

    private static final String USER_P_8 = "userP8";
    private final AuthServiceP8 mAuthService;
    private final SessionManager mSessionManager;

    @Inject public AuthRepositoryP8Impl(@RepositoryCache SharedObservableStore cache, AuthServiceP8 authService, SessionManager sessionManager) {
        super(cache, USER_P_8);
        mAuthService = authService;
        mSessionManager = sessionManager;
    }

    @Override
    public Single<AuthToken> login(String login, String password) {
        return withNetRawMapper(
                mAuthService.login(login, password)
                        .map(response -> response),
                this::handleSuccessAuth
        );
    }

    @Override
    public Single<AuthToken> relogin() {
        AuroraSession session = mSessionManager.getSession();
        return login(session.getLogin(), session.getPassword());
    }

    public AuthToken handleSuccessAuth(ApiResponse<AuthToken> response){
        AuroraSession session = mSessionManager.getSession();

        if (response.getAccountId() != 0) {
            session.setAccountId(response.getAccountId());
        } else {
            session.setAccountId(-1);
        }
        session.setAuthToken(response.getResult().token);

        return response.getResult();
    }

    @Override
    public Single<SystemAppData> getSystemAppData() {
        //TODO real system app data
        return Single.just(new SystemAppData("APP_TOKEN_STUB"));
    }
}