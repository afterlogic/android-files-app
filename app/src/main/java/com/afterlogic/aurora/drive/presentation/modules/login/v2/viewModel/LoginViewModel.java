package com.afterlogic.aurora.drive.presentation.modules.login.v2.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.webkit.CookieManager;

import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.AsyncUiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.modules.login.v2.interactor.LoginInteractor;
import com.annimon.stream.Stream;

import javax.inject.Inject;

import okhttp3.HttpUrl;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginViewModel extends LifecycleViewModel {

    public ObservableField<LoginViewModelState> loginState = new AsyncUiObservableField<>(LoginViewModelState.HOST);

    public Bindable<String> host = Bindable.create();
    public ObservableField<String> hostError = new ObservableField<>();
    public ObservableBoolean isInProgress = new ObservableBoolean();

    public ObservableField<String> loginUrl = new ObservableField<>();

    private final LoginInteractor interactor;
    private final Subscriber subscriber;
    private final AppRouter router;

    private HttpUrl checkedHost;

    @Inject
    public LoginViewModel(LoginInteractor interactor,
                          Subscriber subscriber,
                          AppRouter router) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;

        interactor.getLastInputedHost()
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(lastHost -> {
                    if (TextUtils.isEmpty(host.get())) {
                        host.set(lastHost);
                    }
                }));
    }

    public void onHostWritten() {
        interactor.checkHost(host.get())
                .compose(subscriber::defaultSchedulers)
                .doOnSubscribe(disposable -> isInProgress.set(true))
                .doFinally(() -> isInProgress.set(false))
                .subscribe(subscriber.subscribe(checkedHost -> {

                    this.checkedHost = checkedHost;

                    interactor.storeLastInputedHost(host.get())
                            .onErrorResumeNext(error -> interactor.storeLastInputedHost(null))
                            .onErrorComplete()
                            .compose(subscriber::defaultSchedulers)
                            .subscribe(subscriber.justSubscribe());

                    loginUrl.set(this.checkedHost + "?external-clients-login-form");
                    loginState.set(LoginViewModelState.LOGIN);
                }));
    }

    public void onPageLoadingStarted(String url) {
        MyLog.d("Start load url: " + url);

        if (url.equals(checkedHost.toString())) {
            String cookie = CookieManager.getInstance().getCookie(url);
            parseAuthorizedCookie(cookie);
        }

        if (loginState.get() == LoginViewModelState.LOGIN) {
            isInProgress.set(true);
        }
    }

    public void onPageLoadingFinished(String url) {
        MyLog.d("Loaded url: " + url);
        if (loginState.get() == LoginViewModelState.LOGIN) {
            isInProgress.set(false);
        }
    }

    public void onBackPressed() {
        if (loginState.get() == LoginViewModelState.LOGIN) {
            loginState.set(LoginViewModelState.HOST);
        } else {
            router.exit();
        }
    }

    private void parseAuthorizedCookie(String cookie) {

        String authToken = Stream.of(cookie.split(";"))
                .map(String::trim)
                .map(item -> item.split("="))
                .filter(pair -> pair[0].equals("AuthToken"))
                .findFirst()
                .map(pair -> pair[1])
                .orElse(null);

        if (authToken != null) {
            interactor.handleAuth(authToken, checkedHost)
                    .compose(subscriber::defaultSchedulers)
                    .subscribe(subscriber.subscribe(() -> router.replaceScreen(AppRouter.MAIN)));
        }
    }
}
