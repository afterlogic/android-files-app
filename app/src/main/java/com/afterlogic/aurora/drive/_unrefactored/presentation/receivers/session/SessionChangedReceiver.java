package com.afterlogic.aurora.drive._unrefactored.presentation.receivers.session;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive._unrefactored.data.common.ApiProvider;
import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.model.AuroraSession;

/**
 * Created by sashka on 24.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class SessionChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(SessionTrackerReceiver.SESSION_DATA)) {
            ApiProvider apiProvider = new ApiProvider();
            ((App) context.getApplicationContext()).modulesFactory().inject(apiProvider);

            AuroraSession session = intent.getParcelableExtra(SessionTrackerReceiver.SESSION_DATA);
            apiProvider.getSessionManager().setSession(session);
        }
    }
}
