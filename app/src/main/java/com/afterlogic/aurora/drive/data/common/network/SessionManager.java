package com.afterlogic.aurora.drive.data.common.network;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;
import com.afterlogic.aurora.drive.core.common.util.AccountUtil;
import com.afterlogic.aurora.drive.core.common.util.AppUtil;
import com.afterlogic.aurora.drive.model.AuroraSession;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

@DataScope
public class SessionManager {

    private static final String ACTION_SESSION_CHANGED = "ACTION_SESSION_CHANGED";
    private static final String KEY_SENDER = "KEY_SENDER";

    private final Context context;
    private final AccountHelper accountHelper;

    @Nullable
    private AuroraSession session;

    private String senderName;

    @Inject
    public SessionManager(Context context,
                          AccountHelper accountHelper) {
        this.context = context;
        this.accountHelper = accountHelper;

        senderName = AppUtil.getCurrentProcessName(context) + ":" + this;
    }

    @Nullable
    public AuroraSession getSession() {
        return session;
    }

    public void setSession(@Nullable AuroraSession session) {
        if (session == this.session) return;

        this.session = session;

        accountHelper.updateCurrentAccountSessionData(session);

        notifySessionChanged();
    }

    public void start() {

        updateSessionByAccount();

        IntentFilter sessionChangedIntentFilter = new IntentFilter(ACTION_SESSION_CHANGED);

        BroadcastReceiver sessionChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (!intent.hasExtra(KEY_SENDER)
                        || senderName.equals(intent.getStringExtra(KEY_SENDER))) return;

                updateSessionByAccount();

            }
        };

        context.registerReceiver(sessionChangedReceiver, sessionChangedIntentFilter);
    }

    private void notifySessionChanged() {

        Intent intent = new Intent(ACTION_SESSION_CHANGED);
        intent.setPackage(context.getPackageName());

        intent.putExtra(KEY_SENDER, senderName);

        context.sendBroadcast(intent);

    }

    private void updateSessionByAccount() {

        Account account = AccountUtil.getCurrentAccount(context);

        if (account == null) {

            session = null;

        } else {

            session = accountHelper.getCurrentAccountSessionData();

        }
    }
}
