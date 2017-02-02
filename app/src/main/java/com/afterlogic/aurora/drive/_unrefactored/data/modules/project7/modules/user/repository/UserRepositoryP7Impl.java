package com.afterlogic.aurora.drive._unrefactored.data.modules.project7.modules.user.repository;

import com.afterlogic.aurora.drive._unrefactored.core.annotations.qualifers.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive._unrefactored.data.common.SessionManager;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.BaseRepository;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.UserRepository;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project7.modules.user.service.AuthServiceP7;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class UserRepositoryP7Impl extends BaseRepository implements UserRepository {

    private static final String USER_P_7 = "userP7";

    private final AuthServiceP7 mAuthService;

    private final SessionManager mSessionManager;

    @Inject UserRepositoryP7Impl(@RepositoryCache SharedObservableStore cache, AuthServiceP7 authService, SessionManager sessionManager) {
        super(cache, USER_P_7);
        mAuthService = authService;
        mSessionManager = sessionManager;
    }

    @Override
    public Single<AuthToken> login(String login, String password) {
        return withNetRawMapper(
                mAuthService.login(login, password)
                        .map(response -> response),
                response -> {
                    mSessionManager.getAuroraSession().setAccountId(response.getAccountId());
                    return response.getResult();
                }
        );
    }

    @Override
    public Single<AuthToken> relogin() {
        return Single.defer(() -> {
            AuroraSession session = mSessionManager.getAuroraSession();
            return login(session.getLogin(), session.getPassword());
        });
    }

    @Override
    public Single<SystemAppData> getSystemAppData() {
        return withNetMapper(mAuthService.getSystemAppData())
                .map(result -> {
                    mSessionManager.getAuroraSession().setAppToken(result.getToken());
                    return result;
                });
    }
}
