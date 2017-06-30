package com.afterlogic.aurora.drive.data.modules.auth.repository;

import android.content.Context;

import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.model.ApiResponse;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.auth.p8.service.AuthServiceP8;
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleaner;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.presentation.modulesBackground.session.SessionTrackUtil;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SuppressWarnings("WeakerAccess")
public class AuthRepositoryP8Impl extends BaseAuthRepository implements AuthRepository {

    private static final String USER_P_8 = "userP8";
    private final AuthServiceP8 mAuthService;
    private final SessionManager mSessionManager;
    private final Context mContext;

    @Inject public AuthRepositoryP8Impl(@RepositoryCache SharedObservableStore cache,
                                        AuthServiceP8 authService,
                                        SessionManager sessionManager,
                                        DataCleaner dataCleaner,
                                        Context context) {
        super(cache, USER_P_8, context, sessionManager, dataCleaner);
        mAuthService = authService;
        mSessionManager = sessionManager;
        mContext = context;
    }

    @Override
    public Completable login(String login, String password) {
        return mAuthService.login(login, password)
                .compose(Repository::withRawNetMapper)
                .map(this::handleSuccessAuth)
                .toCompletable()
                .andThen(storeAuthData());
    }

    @Override
    public Completable relogin() {
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

        SessionTrackUtil.fireSessionChanged(session, mContext);

        return response.getResult();
    }

    @Override
    public Single<SystemAppData> getSystemAppData() {
        //TODO real system app data
        return Single.just(new SystemAppData("APP_TOKEN_STUB"));
    }
}
