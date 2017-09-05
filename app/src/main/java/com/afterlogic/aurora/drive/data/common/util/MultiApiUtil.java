package com.afterlogic.aurora.drive.data.common.util;

import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;

import javax.inject.Provider;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MultiApiUtil {

    public static <T> T chooseByApiVersion(SessionManager sessionManager, Provider<T> p7, Provider<T> p8){
        AuroraSession session = sessionManager.getSession();

        if (session == null) {
            throw new IllegalStateException("Not authorized. So can't provide any.");
        }

        switch (session.getApiVersion()){
            case Const.ApiVersion.API_P7:
                return p7.get();
            case Const.ApiVersion.API_P8:
                return p8.get();
            default:
                return null;
        }
    }
}
