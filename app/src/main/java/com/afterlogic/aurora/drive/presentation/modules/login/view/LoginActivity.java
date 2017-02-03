package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiError;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.AuroraApi;
import com.afterlogic.aurora.drive._unrefactored.presentation.receivers.AccountLoginStateReceiver;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.databinding.ActivityLoginBinding;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.login.presenter.LoginPresenter;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

import static com.afterlogic.aurora.drive.R.id.domain;

/**
 * A checkApiVersion screen that offers checkApiVersion via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginView{

    private static final String EXTRA_NEXT_ACTIVITY =
            LoginActivity.class.getName() + ".EXTRA_NEXT_ACTIVITY";
    private static final String EXTRA_FINISH_ON_RESULT =
            LoginActivity.class.getName() + "EXTRA_FINISH_ON_RESULT";

    @Inject @ViewPresenter
    protected LoginPresenter mPresenter;
    @Inject
    protected LoginViewModel mViewModel;

    private ActivityLoginBinding mBinding;

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.login().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setSupportActionBar(mBinding.toolbar);

        mBinding.setViewModel(mViewModel);

        mBinding.password.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                mViewModel.onLogin();
                return true;
            }
            return false;
        });
    }

    /**
     * Login to the app.
     */
    private void checkApiVersion(){

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
                .collect(Observables.ObservableCollectors.concatObservables())
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
                                showPasswordError(getString(R.string.error_pass_or_login));
                            }
                        });
            }

            @Override
            public void onError(ApiError error) {
                if (isDestroyed()) return;

                showProgress(false);

                if (error.getCode() == ApiError.NOT_FOUND || error.getCode() == ApiError.UNKNOWN_HOST){
                    showHostError(getString(R.string.error_unrechable_host));
                }else{
                    showMessage(getString(R.string.error_connection_to_domain), PresentationView.TYPE_MESSAGE_MINOR);
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

