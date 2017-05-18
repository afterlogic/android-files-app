package com.afterlogic.aurora.drive.data.common.network;

import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.core.consts.Const;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class SessionManager {

    private AuroraSession mAuroraSession;

    private ApiConfigurator mApiConfigurator;

    public SessionManager(ApiConfigurator apiConfigurator) {
        mApiConfigurator = apiConfigurator;
    }

    public AuroraSession getSession() {
        return mAuroraSession;
    }

    public void setSession(AuroraSession auroraSession) {
        if (auroraSession == mAuroraSession) return;

        mAuroraSession = auroraSession;
        if (auroraSession != null) {
            mApiConfigurator.setDomain(auroraSession.getDomain(), auroraSession.getApiVersion());
        } else {
            mApiConfigurator.setDomain(null, Const.ApiVersion.API_NONE);
        }
    }
}
