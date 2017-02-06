package com.afterlogic.aurora.drive._unrefactored.presentation.receivers.session;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;

/**
 * Created by sashka on 05.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SessionTrackerReceiver extends BroadcastReceiver {

    public static final String ACTION_SESSION_CHANGED =
            "com.afterlogic.aurora.sessiontracker.ACTION_SESSION_CHANGED";

    public static final String SESSION_DATA =
            SessionTrackerReceiver.class.getName() + ".SESSION_DATA";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(SESSION_DATA)){
            AuroraSession session = intent.getParcelableExtra(SESSION_DATA);

            AccountManager am = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            Account account = AccountUtil.getAccount(session.getLogin(), am);
            if (account != null) {
                AccountUtil.updateAccountCredentials(account, session, am);
            }
        }
    }

    public static void fireSessionChanged(AuroraSession session, Context ctx){
        Intent intent = new Intent(ACTION_SESSION_CHANGED);
        intent.putExtra(SESSION_DATA, session);

        ctx.sendBroadcast(intent);
    }
}
