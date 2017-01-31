package com.afterlogic.aurora.drive.presentation.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.Const;
import com.afterlogic.aurora.drive.core.MyLog;
import com.afterlogic.aurora.drive.core.rx.ConcatObservableCollector;
import com.afterlogic.aurora.drive.core.util.AccountUtil;
import com.afterlogic.aurora.drive.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive.data.common.api.ApiError;
import com.afterlogic.aurora.drive.data.common.api.ApiUtil;
import com.afterlogic.aurora.drive.data.common.api.AuroraApi;
import com.afterlogic.aurora.drive.data.modules.checker.ApiChecker;
import com.afterlogic.aurora.drive.data.common.ApiConfigurator;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.presentation.receivers.AccountLoginStateReceiver;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

/**
 * A checkApiVersion screen that offers checkApiVersion via email/password.
 */
public class AuroraLoginActivity extends AppCompatActivity{

    private static final String EXTRA_NEXT_ACTIVITY =
            AuroraLoginActivity.class.getName() + ".EXTRA_NEXT_ACTIVITY";
    private static final String EXTRA_FINISH_ON_RESULT =
            AuroraLoginActivity.class.getName() + "EXTRA_FINISH_ON_RESULT";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mDomainView;
    private View mProgressView;
    private Button mLoginButton;

    private boolean mIsDestroyed = false;

    @Inject
    protected ApiConfigurator mApiConfigurator;

    @Inject
    protected ApiChecker mApiChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((App) getApplication()).getDataComponent().inject(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null){
            setSupportActionBar(mToolbar);
        }

        // Set up the checkApiVersion form.
        mDomainView = (EditText) findViewById(R.id.domain);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginButton.setOnClickListener(view -> attemptLogin());

        mProgressView = findViewById(R.id.login_progress);

        AuroraSession session = AuroraApi.getCurrentSession();

        if (session != null) {
            HttpUrl url = session.getDomain();
            if (url != null) {
                mDomainView.setText(url.toString());
            }
            mEmailView.setText(session.getLogin());
            mPasswordView.setText(session.getPassword());
        }
    }

    @Override
    protected void onDestroy() {
        mIsDestroyed = true;
        super.onDestroy();
    }

    public boolean isDestroyed() {
        return mIsDestroyed;
    }

    /**
     * Attempts to sign in or register the account specified by the checkApiVersion form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual checkApiVersion attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the checkApiVersion attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String domain = mDomainView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check for a valid domain
        if (TextUtils.isEmpty(domain)){
            mDomainView.setError(getString(R.string.error_field_required));
            focusView = mDomainView;
            cancel = true;
        }else if (!isDomainValid(domain)){
            mDomainView.setError(getString(R.string.error_unrechable_host));
            focusView = mDomainView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt checkApiVersion and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user checkApiVersion attempt.
            checkApiVersion();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isDomainValid(String domain){
        return ApiUtil.parseCompleted(domain, "https") != null;
    }

    private void showProgress(boolean show) {
        mEmailView.setEnabled(!show);
        mPasswordView.setEnabled(!show);
        mDomainView.setEnabled(!show);

        mLoginButton.setEnabled(!show);
        mLoginButton.setText(show ? null : getString(R.string.action_sign_in));

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Login to the app.
     */
    private void checkApiVersion(){
        showProgress(true);
        String manualDomain = mDomainView.getText().toString().trim();

        HttpUrl domain = ApiUtil.parseCompleted(manualDomain, "https");

        assert domain != null;

        boolean hasManualScheme = manualDomain.startsWith(domain.scheme());

        List<HttpUrl> domains = new ArrayList<>();

        domains.add(domain);

        if (!hasManualScheme){
            HttpUrl httpDomain = ApiUtil.parseCompleted(manualDomain, "http");
            if (httpDomain != null){
                domains.add(httpDomain);
            }
        }

        Stream.of(domains)
                .map(mApiChecker::getApiVersion)
                .map(Single::toObservable)
                .collect(ConcatObservableCollector.create())
                .filter(version -> version != Const.ApiVersion.API_NONE)
                .first(Const.ApiVersion.API_NONE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        version -> {
                            if (version != Const.ApiVersion.API_NONE) {
                                MyLog.d(this, "ApiVersion: " + version);
                                login(version, domain);
                            } else {
                                showProgress(false);
                                mDomainView.setError(getString(R.string.error_unrechable_host));
                                mDomainView.requestFocus();
                            }
                        },
                        error -> {
                            showProgress(false);
                            Toast.makeText(this, R.string.error_connection_to_domain, Toast.LENGTH_LONG).show();
                            MyLog.e(this, error);
                        }
                );


    }

    private void login(int apiVersion, HttpUrl domain){

        AuroraApi.setCurrentSession(
                new AuroraSession(
                        domain,
                        mEmailView.getText().toString(),
                        mPasswordView.getText().toString(),
                        apiVersion
                )
        );

        AuroraApi.getSystemAppData(new ApiCallback<SystemAppData>() {
            @Override
            public void onSucces(SystemAppData result) {
                if (isDestroyed()) return;

                AuroraSession session = AuroraApi.getCurrentSession();
                AuroraApi.login(session.getLogin(), session.getPassword(),
                        new ApiCallback<AuthToken>() {

                            @Override
                            public void onSucces(AuthToken result) {
                                if (isDestroyed()) return;

                                showProgress(false);

                                onSuccessLogin();
                            }

                            @Override
                            public void onError(ApiError error) {
                                if (isDestroyed()) return;

                                showProgress(false);
                                mPasswordView.setText(null);
                                mPasswordView.setError(getString(R.string.error_pass_or_login));
                                mPasswordView.requestFocus();
                            }
                        });
            }

            @Override
            public void onError(ApiError error) {
                if (isDestroyed()) return;

                showProgress(false);

                if (error.getCode() == ApiError.NOT_FOUND || error.getCode() == ApiError.UNKNOWN_HOST){
                    mDomainView.setError(getString(R.string.error_unrechable_host));
                    mDomainView.requestFocus();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.error_connection_to_domain,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onSuccessLogin(){

        AccountManager am = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
        AuroraSession session = AuroraApi.getCurrentSession();

        boolean success = true;

        Account account = null;
        for (Account a:am.getAccountsByType(AccountUtil.ACCOUNT_TYPE)){
            if (a.name.equals(session.getLogin())){
                account = a;
                break;
            }
        }

        boolean isNew = account == null;
        if (isNew) {
            account = new Account(session.getLogin(), AccountUtil.ACCOUNT_TYPE);
            if (!am.addAccountExplicitly(account, session.getPassword(), null)) {
                success = false;
                Toast.makeText(this, R.string.error_adding_aurora_account, Toast.LENGTH_LONG).show();
            }
        }

        if (success){

            AccountUtil.updateAccountCredentials(account, session, am);

            if (!getIntent().getBooleanExtra(EXTRA_FINISH_ON_RESULT, false)) {
                Class nextActivityClass = (Class) getIntent().getSerializableExtra(EXTRA_NEXT_ACTIVITY);
                startActivity(new Intent(getApplicationContext(), nextActivityClass));
            } else {
                setResult(Activity.RESULT_OK);
            }

            Intent loggedBroadcast = new Intent(AccountLoginStateReceiver.ACTION_AURORA_LOGIN);
            loggedBroadcast.putExtra(AccountLoginStateReceiver.ACCOUNT, account);
            loggedBroadcast.putExtra(AccountLoginStateReceiver.AURORA_SESSION, session);
            loggedBroadcast.putExtra(AccountLoginStateReceiver.IS_NEW, isNew);
            sendBroadcast(loggedBroadcast);

            finish();
        }
    }

    public static class IntentCreator{

        public static Intent makeNextActivity(Intent i, Class activityClass){
            i.putExtra(EXTRA_NEXT_ACTIVITY, activityClass);
            return i;
        }

        public static Intent loginAndReturn(Intent i){
            i.putExtra(EXTRA_FINISH_ON_RESULT, true);
            return i;
        }
    }
}

