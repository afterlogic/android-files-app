package com.afterlogic.aurora.drive.presentation.modulesBackground.session;

import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.model.AuroraSession;

/**
 * Created by sashka on 05.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SessionTrackUtil{

    static final String ACTION_SESSION_CHANGED =
            "com.afterlogic.aurora.sessiontracker.ACTION_SESSION_CHANGED";

    static final String SESSION_DATA =
            SessionTrackUtil.class.getName() + ".SESSION_DATA";

    static final String SESSOIN_CHANGED =
            "com.afterlogic.aurora.sessiontracker.SESSOIN_CHANGED";

    public static void fireSessionChanged(AuroraSession session, Context ctx){
        Intent intent = new Intent(ACTION_SESSION_CHANGED);
        intent.putExtra(SESSION_DATA, session);
        intent.putExtra(SESSOIN_CHANGED, true);
        ctx.sendBroadcast(intent);
    }
}
