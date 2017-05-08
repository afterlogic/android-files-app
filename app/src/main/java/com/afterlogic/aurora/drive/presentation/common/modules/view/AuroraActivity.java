package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction.AccountActionReceiver;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 08.05.17.
 */

public class AuroraActivity extends AppCompatActivity {

    private AuroraActivityHelper mHelper = new AuroraActivityHelper();

    private BroadcastReceiver mCheckAuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkAuthPrefs(null);
        }
    };

    private boolean mCheckAuthPrefs = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getApplication()).getInjectors().inject(mHelper);
        onPerformCreate(savedInstanceState);
        checkAuthPrefs(null);
    }

    protected void onPerformCreate(@Nullable Bundle savedInstanceState) {
        //no-op
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuthPrefs(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthPrefs(null);

        registerReceiver(mCheckAuthReceiver, new IntentFilter(AccountActionReceiver.ACTION_LOGIN_CHANGED));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mCheckAuthReceiver);
        super.onPause();
    }

    protected void setCheckAuth(boolean checkAuth) {
        mCheckAuthPrefs = checkAuth;
    }

    private void checkAuthPrefs(@Nullable Runnable onAuthorized) {
        if (!mCheckAuthPrefs || mHelper.getSession() != null) {
            if (onAuthorized != null) {
                onAuthorized.run();
            }
        } else {
            startActivity(LoginActivity.intent(true, this));
            finish();
        }
    }

    public class AuroraActivityHelper {

        @Inject
        protected SessionManager mSessionManager;

        @Nullable
        public AuroraSession getSession() {
            return mSessionManager.getSession();
        }
    }
}