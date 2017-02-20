package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.HttpUrlUtil;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.StringBinder;
import com.afterlogic.aurora.drive.presentation.modules.login.model.presenter.LoginPresenter;
import com.annimon.stream.Stream;

import io.reactivex.Single;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginViewModel{

    private final AppResources mAppResources;

    private LoginModel mController = new LoginModelImpl();
    private OptWeakRef<LoginPresenter> mPresenter = OptWeakRef.empty();

    private final StringBinder mLogin = createInputStringBinder();
    private final StringBinder mPassword = createInputStringBinder();
    private final StringBinder mHost = createInputStringBinder();

    private boolean mErrorState = false;
    private final ObservableField<String> mPasswordError = new ObservableField<>();
    private final ObservableField<String> mLoginError = new ObservableField<>();
    private final ObservableField<String> mHostError = new ObservableField<>();
    private final ObservableBoolean mProgressState = new ObservableBoolean();

    public LoginViewModel(AppResources appResources) {
        mAppResources = appResources;
    }

    public LoginModel getController(){
        return mController;
    }

    public StringBinder getLogin(){
        return mLogin;
    }

    public StringBinder getPassword() {
        return mPassword;
    }

    public StringBinder getHost() {
        return mHost;
    }

    public ObservableField<String> getPasswordError() {
        return mPasswordError;
    }

    public ObservableField<String> getLoginError() {
        return mLoginError;
    }

    public ObservableField<String> getHostError() {
        return mHostError;
    }

    public ObservableBoolean getIsInProgress(){
        return mProgressState;
    }

    public void onLogin(){
        mPresenter.ifPresent(LoginPresenter::onLogin);
    }

    private StringBinder createInputStringBinder(){
        Holder<String> value = new Holder<>();
        return new StringBinder(
                value::get,
                inputValue -> {
                    value.set(inputValue.trim());
                    clearErrors();
                }
        );
    }

    private void clearErrors(){
        if (!mErrorState) return;
        mErrorState = false;

        mPasswordError.set(null);
        mLoginError.set(null);
        mHostError.set(null);
    }

    private class LoginModelImpl implements LoginModel {

        private LoginModelImpl() {
        }

        @Override
        public void setPresenter(LoginPresenter presenter) {
            mPresenter.set(presenter);
        }

        @Override
        public void setSessionData(AuroraSession session) {
            mHost.set(session.getDomain().toString());
            mLogin.set(session.getLogin());
            mPassword.set("");
        }

        @Override
        public boolean isManualUrlScheme() {
            return startWith(mHost.get(), "http", "https");
        }

        @Override
        public Single<AuroraSession> collectNewSessionData() {
            return Single.fromCallable(() -> {
                RuntimeException error = null;
                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(mPassword.get())) {
                    mPasswordError.set(mAppResources.getString(R.string.error_field_required));
                    error = new RuntimeException("Password required.");
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(mLogin.get())) {
                    mLoginError.set(mAppResources.getString(R.string.error_field_required));
                    error = new RuntimeException("Login required.");
                } else if (!isEmailValid(mLogin.get())) {
                    mLoginError.set(mAppResources.getString(R.string.error_invalid_email));
                    error = new RuntimeException("Invalid login.");
                }

                //Check for a valid domain
                if (TextUtils.isEmpty(mHost.get())){
                    mHostError.set(mAppResources.getString(R.string.error_field_required));
                    error = new RuntimeException("Host required.");
                }else if (!isDomainValid(mHost.get())){
                    mHostError.set(mAppResources.getString(R.string.error_unrechable_host));
                    error = new RuntimeException("Invalid host.");
                }

                if (error != null) {
                    mErrorState = true;
                    throw error;
                }

                return new AuroraSession(
                        HttpUrlUtil.parseCompleted(mHost.get(), "https"),
                        mLogin.get(),
                        mPassword.get(),
                        Const.ApiVersion.API_NONE
                );
            });
        }

        @Override
        public void setPasswordError() {
            mLoginError.set(null);
            mHostError.set(null);
            mPasswordError.set(mAppResources.getString(R.string.error_pass_or_login));
            mErrorState = true;
        }

        @Override
        public void setDomainError() {
            mLoginError.set(null);
            mPasswordError.set(null);
            mHostError.set(mAppResources.getString(R.string.error_unrechable_host));
            mErrorState = true;
        }

        @Override
        public void setProgressState(boolean inProgress) {
            mProgressState.set(inProgress);
        }

        private boolean startWith(String source, String... prefixes){
            return Stream.of(prefixes).anyMatch(prefix ->
                    !(TextUtils.isEmpty(source) || TextUtils.isEmpty(prefix))
                            && source.startsWith(prefix)
            );
        }

        private boolean isEmailValid(String email) {
            return email.contains("@");
        }

        private boolean isDomainValid(String domain){
            return HttpUrlUtil.parseCompleted(domain, "https") != null;
        }
    }
}
