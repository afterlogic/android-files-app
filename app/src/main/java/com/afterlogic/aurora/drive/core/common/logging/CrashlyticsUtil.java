package com.afterlogic.aurora.drive.core.common.logging;

import com.afterlogic.aurora.drive.model.AuroraSession;
import com.crashlytics.android.core.CrashlyticsCore;

/**
 * Created by aleksandrcikin on 28.07.17.
 * mail: mail@sunnydaydev.me
 */

public class CrashlyticsUtil {

    public static void updateUserData(AuroraSession session) {
        if (session == null) return;

        CrashlyticsCore.getInstance().setUserEmail(session.getLogin());
        CrashlyticsCore.getInstance().setInt("Api version", session.getApiVersion());
        CrashlyticsCore.getInstance().setString("Domain", session.getDomain().toString());
    }

}
