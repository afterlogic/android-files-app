package com.afterlogic.aurora.drive._unrefactored.presentation.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;

/**
 * Created by sashka on 29.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AccountActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLog.d(this, "onReceive: " + intent);
        if (AccountUtil.getCurrentAccount(context) == null){
            Api.setCurrentSession(null);
            context.sendBroadcast(new Intent(AccountLoginStateReceiver.ACTION_AURORA_LOGOUT));
        }
    }
}
