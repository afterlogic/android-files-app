package com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.AppUtil;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleaner;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.Completable;

/**
 * Created by sashka on 29.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AccountActionReceiver extends BroadcastReceiver {

    private static final String LOGIN_ACCOUNTS_CHANGED_ACTION =
            "com.afterlogic.aurora.files.LOGIN_ACCOUNTS_CHANGED_ACTION";

    @Inject
    protected DataCleaner dataCleaner;

    @Inject
    protected AppPrefs appPrefs;

    @Inject
    protected AccountHelper accountHelper;

    @Inject
    protected SessionManager sessionManager;

    public static void notifyAccountsChanged(Context context) {
        context.sendBroadcast(
                new Intent(LOGIN_ACCOUNTS_CHANGED_ACTION)
                        .setPackage(context.getPackageName())
        );
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!(
                AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION.equals(intent.getAction())
                || LOGIN_ACCOUNTS_CHANGED_ACTION.equals(intent.getAction())
        )) {
            return;
        }

        AndroidInjection.inject(this, context);

        MyLog.d(this, "onReceive: " + intent);

        sessionManager.notifySessionChanged();

        appPrefs.loggedIn().set(accountHelper.hasCurrentAccount());

        if (!accountHelper.hasCurrentAccount()){
            dataCleaner.cleanAllUserData()
                    .andThen(Completable.fromAction(() -> {
                        context.stopService(FileObserverService.intent(context));
                        AppUtil.setComponentEnabled(FileObserverService.class, false, context);
                    }))
                    .subscribe(() -> {}, MyLog::majorException);
        }

    }
}
