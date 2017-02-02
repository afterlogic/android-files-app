package com.afterlogic.aurora.drive._unrefactored.data.modules.project7.common;

import com.afterlogic.aurora.drive._unrefactored.data.common.ParamsBuilder;
import com.afterlogic.aurora.drive._unrefactored.data.common.SessionManager;
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
        AuroraSession session = mSessionManager.getAuroraSession();
        return new ParamsBuilder()
                .put(Api7.Fields.ACTION, method)
                .put(Api7.Fields.ACCOUNT_ID, session.getAccountId())
                .put(Api7.Fields.AUTH_TOKEN, session.getAuthToken())
                .put(Api7.Fields.TOKEN, session.getAppToken());
    }
}