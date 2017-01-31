package com.afterlogic.aurora.drive.presentation.receivers;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.afterlogic.aurora.drive.core.MyLog;
import com.afterlogic.aurora.drive.core.util.AccountUtil;
import com.afterlogic.aurora.drive.data.common.api.AuroraApi;
import com.afterlogic.aurora.drive.data.common.db.DBHelper;
import com.afterlogic.aurora.drive.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.services.ClearCacheService;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Created by sashka on 21.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class AccountLoginStateReceiver extends BroadcastReceiver {

    public static final String ACTION_AURORA_LOGIN =
            "com.afterlogic.aurora.aurorapi.ACTION_AURORA_LOG_IN";
    public static final String ACTION_AURORA_LOGOUT=
            "com.afterlogic.aurora.aurorapi.ACTION_AURORA_LOG_OUT";

    public static final String ACCOUNT =
            AccountLoginStateReceiver.class.getName() + ".ACCOUNT";
    public static final String AURORA_SESSION =
            AccountLoginStateReceiver.class.getName() + ".AURORA_SESSION";
    public static final String IS_NEW =
            AccountLoginStateReceiver.class.getName() + ".IS_NEW";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case AccountLoginStateReceiver.ACTION_AURORA_LOGIN:
                Account account = intent.getParcelableExtra(AccountLoginStateReceiver.ACCOUNT);
                AuroraSession session = intent.getParcelableExtra(AccountLoginStateReceiver.AURORA_SESSION);

                AuroraApi.setCurrentSession(session);

                boolean isNew = intent.getBooleanExtra(AccountLoginStateReceiver.IS_NEW, true);


                MyLog.d(this, "Login event received: " + account.name + ", " + isNew);

                onSuccessLogged(context, account, session, isNew);
                break;
            case AccountLoginStateReceiver.ACTION_AURORA_LOGOUT:
                MyLog.d(this, "Logout event received.");
                onLogout(context);
                break;
        }
    }

    public void onSuccessLogged(Context ctx, Account account, AuroraSession session, boolean isNew) {
        if (isNew){
            ContentResolver.setSyncAutomatically(
                    account,
                    AccountUtil.FILE_SYNC_AUTHORITY,
                    true
            );
            ContentResolver.addPeriodicSync(
                    account,
                    AccountUtil.FILE_SYNC_AUTHORITY,
                    Bundle.EMPTY,
                    TimeUnit.DAYS.toSeconds(1)
            );
        }
    }

    public void onLogout(Context context) {
        AuroraApi.setCurrentSession(null);

        //[START Clear all offline and cached data]
        DBHelper db = new DBHelper(context);
        try {
            TableUtils.clearTable(db.getConnectionSource(), WatchingFile.class);
        } catch (SQLException e) {
            MyLog.majorException(this, e);
        } finally {
            db.close();
        }
        context.startService(new Intent(context, ClearCacheService.class));
        //[END Clear all offline and cached data]
    }
}
