package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.webkit.CookieManager;

import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.AuthorizationResolver;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.AsyncUiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.AuthResult;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractor;
import com.annimon.stream.Stream;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
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
    private final AuthorizationResolver authorizationResolver;

    private HttpUrl checkedHost;

    private PublishSubject<Boolean> reloginPublisher = PublishSubject.create();
    private boolean relogin;

    private final DisposableBag globalBag = new DisposableBag();

    @Inject
    public LoginViewModel(LoginInteractor interactor,
                          Subscriber subscriber,
                          AppRouter router,
                          AuthorizationResolver resolver) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.authorizationResolver = resolver;

        Single.zip(
                reloginPublisher
                        .firstOrError(),
                interactor.getLastInputedHost()
                        .toSingle("")
                        .compose(subscriber::defaultSchedulers),
                LastLoggedHost::new
        )//--->
                .subscribe(subscriber.subscribe(this::handleLastCheckedHostUrl));
    }

    public void setArgs(boolean relogin) {
        this.relogin = relogin;
        reloginPublisher.onNext(relogin);
    }

    public void onHostWritten() {
        interactor.checkHost(host.get())
                .compose(subscriber::defaultSchedulers)
                .doOnSubscribe(disposable -> isInProgress.set(true))
                .doFinally(() -> isInProgress.set(false))
                .compose(globalBag::track)
                .subscribe(subscriber.subscribe(checkedHost -> {

                    this.checkedHost = checkedHost;

                    interactor.storeLastInputedHost(checkedHost.toString())
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

    public boolean shouldOverrideUrlLoading(String url) {

        if (!url.equals(checkedHost.toString())) return false;

        String cookie = CookieManager.getInstance().getCookie(url);
        parseAuthorizedCookie(cookie);

        return true;

    }

    public void onBackPressed() {
        if (loginState.get() == LoginViewModelState.LOGIN && !relogin) {
            loginState.set(LoginViewModelState.HOST);
        } else {
            authorizationResolver.onAuthorizationFailed();
            router.finishChain();
        }
    }

    @Override
    protected void onCleared() {
        globalBag.dispose();
        super.onCleared();
    }

    private void handleLastCheckedHostUrl(LastLoggedHost lastHost) {

        if (!TextUtils.isEmpty(lastHost.host)) {

            host.set(lastHost.host);
            checkedHost = HttpUrl.parse(lastHost.host);
            loginUrl.set(this.checkedHost + "?external-clients-login-form");

            if (lastHost.relogin) {
                loginState.set(LoginViewModelState.LOGIN);
            }
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
                    .compose(globalBag::track)
                    .subscribe(subscriber.subscribe(this::handleAuthResult));
        }
    }

    private void handleAuthResult(AuthResult result) {

        switch (result) {

            case DONE:

                authorizationResolver.onAuthorized();

                if (relogin) {
                    router.exit();
                } else {
                    router.newRootScreen(AppRouter.MAIN);
                }
                break;

            case ACCOUNT_CHANGED:

                authorizationResolver.onAccountChanged();

                router.newRootScreen(AppRouter.MAIN);
                break;

            case CANCELLED:
                loginUrl.set(this.checkedHost + "?external-clients-login-form");
                break;

        }
    }
}
