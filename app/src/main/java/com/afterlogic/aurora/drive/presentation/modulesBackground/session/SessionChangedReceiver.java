package com.afterlogic.aurora.drive.presentation.modulesBackground.session;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;

import javax.inject.Inject;

/**
 * Created by sashka on 24.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SessionChangedReceiver extends BroadcastReceiver {

    @Inject
    protected SessionManager mSessionManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(SessionTrackUtil.SESSOIN_CHANGED, false)) {
            ((App) context.getApplicationContext()).modulesFactory().inject(this);

            AuroraSession session = intent.getParcelableExtra(SessionTrackUtil.SESSION_DATA);
            mSessionManager.setSession(session);
        }
    }
}
