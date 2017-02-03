package com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive._unrefactored.presentation.receivers.session.SessionTrackerReceiver;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.FilesListActivity;

/**
 * Created by sashka on 21.03.16.
 * mail: sunnyday.development@gmail.com
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_LOGIN = 1;

    private Toolbar mToolbar;

    private boolean mIsDestroyed = false;
    private boolean mIsActive = false;
    private boolean mIsLoginStarted = false;
    private boolean mIsReleloginRequest = false;

    private BroadcastReceiver mLoginFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleLoginFailed();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onSetContentView();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null){
            setSupportActionBar(mToolbar);
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            initActionBar(ab);
        }
    }

    public boolean isCatchLoginFail(){
        return false;
    }

    protected void onSetContentView(){
        setContentView(getActivityLayout());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isCatchLoginFail()) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mLoginFailedReceiver, new IntentFilter(Api.ACTION_AUTH_FAILED));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActive = true;

        if (isCatchLoginFail() && Api.getCurrentSession() == null){
            onLoginFailed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_LOGIN:
                mIsLoginStarted = false;
                if (resultCode == RESULT_CANCELED){
                    finish();
                }
                break;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (requestCode == REQUEST_CODE_LOGIN && !mIsReleloginRequest){
            throw new IllegalArgumentException("Request code can't be 1 (Login request code)!");
        }
        super.startActivityForResult(intent, requestCode, options);
        mIsReleloginRequest = false;
    }

    @Override
    protected void onPause() {
        mIsActive = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (isCatchLoginFail()){
            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(mLoginFailedReceiver);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for actionbar initialization.
     */
    public void initActionBar(ActionBar ab) {
        //Stub
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Get default activity layout.
     */
    @LayoutRes
    public abstract int getActivityLayout();

    @Override
    public boolean isDestroyed() {
        return mIsDestroyed;
    }

    public boolean isActive() {
        return mIsActive;
    }

    private void handleLoginFailed(){
        if (!mIsLoginStarted){
            mIsLoginStarted = onLoginFailed();
        }
    }

    protected boolean onLoginFailed(){
        AuroraSession session = Api.getCurrentSession();
        if (session != null){
            session.setAuthToken(null);
            session.setAppToken(null);
            SessionTrackerReceiver.fireSessionChanged(session, this);
        }
        startLoginActivity(session == null);
        return true;
    }

    public void startLoginActivity(boolean resetActivityStack){
        mIsReleloginRequest = true;
        if (resetActivityStack){
            Intent i = LoginActivity.IntentCreator.makeNextActivity(
                    new Intent(this, LoginActivity.class),
                    FilesListActivity.class
            );
            Intent resultIntent = IntentCompat.makeRestartActivityTask(i.getComponent());
            resultIntent.putExtras(i);
            startActivity(resultIntent);
        } else {
            startActivityForResult(
                    LoginActivity.IntentCreator
                            .loginAndReturn(new Intent(this, LoginActivity.class))
                    , REQUEST_CODE_LOGIN
            );
        }
    }
}
