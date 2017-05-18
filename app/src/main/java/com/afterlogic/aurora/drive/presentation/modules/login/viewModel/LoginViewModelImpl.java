package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.contextWrappers.Toaster;
import com.afterlogic.aurora.drive.core.common.util.ErrorUtil;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.core.common.util.HttpUrlUtil;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.error.AccountManagerError;
import com.afterlogic.aurora.drive.model.error.AuthError;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.StringBinder;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractor;
import com.afterlogic.aurora.drive.presentation.modules.login.router.LoginRouter;
import com.annimon.stream.Stream;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginViewModelImpl extends BaseViewModel implements LoginViewModel {

    private final AppResources mAppResources;
    private final LoginInteractor mInteractor;
    private final LoginRouter mRouter;
    private final Toaster toaster;

    private final StringBinder mLogin = createInputStringBinder();
    private final StringBinder mPassword = createInputStringBinder();
    private final StringBinder mHost = createInputStringBinder();

    private boolean mErrorState = false;
    private final ObservableField<String> mPasswordError = new ObservableField<>();
    private final ObservableField<String> mLoginError = new ObservableField<>();
    private final ObservableField<String> mHostError = new ObservableField<>();
    private final ObservableBoolean mProgressState = new ObservableBoolean();

    private boolean mFirstStart = true;
    private boolean mHasSessionAtStart = false;

    @Inject
    LoginViewModelImpl(AppResources appResources,
                              LoginInteractor mInteractor,
                              LoginRouter mRouter,
                              Toaster toaster) {
        mAppResources = appResources;
        this.mInteractor = mInteractor;
        this.mRouter = mRouter;
        this.toaster = toaster;
    }

    @Override
    public StringBinder getLogin(){
        return mLogin;
    }

    @Override
    public StringBinder getPassword() {
        return mPassword;
    }

    @Override
    public StringBinder getHost() {
        return mHost;
    }

    @Override
    public ObservableField<String> getPasswordError() {
        return mPasswordError;
    }

    @Override
    public ObservableField<String> getLoginError() {
        return mLoginError;
    }

    @Override
    public ObservableField<String> getHostError() {
        return mHostError;
    }

    @Override
    public ObservableBoolean getIsInProgress(){
        return mProgressState;
    }

    @Override
    public void onLogin(){
        boolean error = false;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mPassword.get())) {
            mPasswordError.set(mAppResources.getString(R.string.error_field_required));
            error = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mLogin.get())) {
            mLoginError.set(mAppResources.getString(R.string.error_field_required));
            error = true;
        }

        //Check for a valid domain
        if (TextUtils.isEmpty(mHost.get())){
            mHostError.set(mAppResources.getString(R.string.error_field_required));
            error = true;
        }else if (!isDomainValid(mHost.get())){
            mHostError.set(mAppResources.getString(R.string.error_unrechable_host));
            error = true;
        }

        if (error) return;

        AuroraSession session = new AuroraSession(
                HttpUrlUtil.parseCompleted(mHost.get(), "https"),
                mLogin.get(),
                mPassword.get(),
                Const.ApiVersion.API_NONE
        );

        mInteractor.login(session, startWith(mHost.get(), "http", "https"))
                .doOnSubscribe(disposable -> mProgressState.set(true))
                .doFinally(() -> mProgressState.set(false))
                .subscribe(
                        mRouter::openNext,
                        this::handleLoginError
                );
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        AtomicBoolean hasSession = new AtomicBoolean(false);
        mInteractor.getCurrentSession()
                .subscribe(
                        session -> {
                            hasSession.set(true);
                            mHost.set(session.getDomain().toString());
                            mLogin.set(session.getLogin());
                            mPassword.set("");
                        },
                        this::onErrorObtained,
                        () -> {
                            if (mFirstStart) {
                                mFirstStart = false;
                                mHasSessionAtStart = hasSession.get();
                            }
                        }
                );
    }

    @Override
    public void onViewResumed() {
        //Check may be logged from another app
        mInteractor.getCurrentSession()
                .subscribe(
                        session -> {
                            if (!mHasSessionAtStart) {
                                mRouter.openNext();
                            }
                        },
                        this::onErrorObtained
                );
    }

    private void handleLoginError(Throwable throwable){
        switch (ErrorUtil.getErrorCode(throwable)){

            case AccountManagerError.CODE:
                toaster.showLong(R.string.error_adding_aurora_account);
                break;

            case AuthError.CODE:
                mLoginError.set(null);
                mHostError.set(null);
                mPasswordError.set(mAppResources.getString(R.string.error_pass_or_login));
                mErrorState = true;
                break;

            case UnknownApiVersionError.CODE:
                mLoginError.set(null);
                mPasswordError.set(null);
                mHostError.set(mAppResources.getString(R.string.error_unrechable_host));
                mErrorState = true;
                break;

            default:
                onErrorObtained(throwable);
                toaster.showLong(R.string.error_connection_to_domain);
        }
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

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isDomainValid(String domain){
        return HttpUrlUtil.parseCompleted(domain, "https") != null;
    }

    private boolean startWith(String source, String... prefixes){
        return Stream.of(prefixes).anyMatch(prefix ->
                !(TextUtils.isEmpty(source) || TextUtils.isEmpty(prefix))
                        && source.startsWith(prefix)
        );
    }
}
