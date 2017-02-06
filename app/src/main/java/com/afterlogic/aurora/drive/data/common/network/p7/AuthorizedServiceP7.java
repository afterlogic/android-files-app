package com.afterlogic.aurora.drive.data.common.network.p7;

import com.afterlogic.aurora.drive.data.common.network.ParamsBuilder;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuthorizedServiceP7 {

    private final SessionManager mSessionManager;

    public AuthorizedServiceP7(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    @SuppressWarnings("WeakerAccess")
    public SessionManager getSessionManager() {
        return mSessionManager;
    }

    protected ParamsBuilder getDefaultParams(String method){
        AuroraSession session = mSessionManager.getSession();
        return new ParamsBuilder()
                .put(Api7.Fields.ACTION, method)
                .put(Api7.Fields.ACCOUNT_ID, session.getAccountId())
                .put(Api7.Fields.AUTH_TOKEN, session.getAuthToken())
                .put(Api7.Fields.TOKEN, session.getAppToken());
    }
}