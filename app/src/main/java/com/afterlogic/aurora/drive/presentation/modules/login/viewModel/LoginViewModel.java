package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.AuthorizationResolver;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.error.AuthError;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.commands.FocusCommand;
import com.afterlogic.aurora.drive.presentation.common.binding.commands.SimpleCommand;
import com.afterlogic.aurora.drive.presentation.common.binding.commands.WebViewGoBackCommand;
import com.afterlogic.aurora.drive.presentation.common.binding.models.EditorEvent;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.AsyncUiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.AuthResult;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractor;
import com.annimon.stream.Stream;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.HttpUrl;

import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModelState.HOST;
import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModelState.LOGIN;
import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModelState.LOGIN_INITIALIZATION;
import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginWebViewState.ERROR;
import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginWebViewState.INITIALIZATION;
import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginWebViewState.NORMAL;
import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginWebViewState.NOT_AVAILABLE;
import static com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginWebViewState.PROGRESS;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginViewModel extends LifecycleViewModel {

    public ObservableField<LoginViewModelState> viewModelState = new AsyncUiObservableField<>(HOST);

    public Bindable<String> host = Bindable.create();
    public Bindable<String> login = Bindable.create();
    public Bindable<String> password = Bindable.create();

    public ObservableField<String> hostError = new ObservableField<>();
    public ObservableField<String> loginError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();

    public ObservableBoolean loginWebViewFullscreen = new ObservableBoolean(false);
    public WebViewGoBackCommand webViewGoBackCommand = new WebViewGoBackCommand();
    public SimpleCommand reloadWebViewCommand = new SimpleCommand();
    public SimpleCommand webViewStopLoadingCommand = new SimpleCommand();
    public SimpleCommand webViewClearHistoryCommand = new SimpleCommand();

    public ObservableBoolean pageReloading = new ObservableBoolean(false);
    public ObservableField<LoginWebViewState> webViewState = new ObservableField<>(INITIALIZATION);

    public FocusCommand focus = new FocusCommand();

    public ObservableBoolean isInProgress = new ObservableBoolean();

    public ObservableField<String> loginUrl = new ObservableField<>();

    private final LoginInteractor interactor;
    private final Subscriber subscriber;
    private final AppRouter router;
    private final AuthorizationResolver authorizationResolver;
    private final AppResources appResources;

    private String checkedHost;

    private PublishSubject<Boolean> reloginPublisher = PublishSubject.create();
    private boolean relogin;

    private boolean networkAvailable = true;
    private boolean errorStateHandled = true;
    private boolean isPageLoadingAfterCancellAuth = false;

    private final DisposableBag globalBag = new DisposableBag();

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    @Inject
    public LoginViewModel(LoginInteractor interactor,
                          Subscriber subscriber,
                          AppRouter router,
                          AuthorizationResolver resolver,
                          AppResources appResources) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.authorizationResolver = resolver;
        this.appResources = appResources;

        initProperties();
        initObservables();

    }

    public void setArgs(boolean relogin) {
        this.relogin = relogin;
        reloginPublisher.onNext(relogin);
    }

    public void onHostWritten() {

        if (isInProgress.get()) return;

        if (TextUtils.isEmpty(host.get())) {
            hostError.set(appResources.getString(R.string.error_field_required));
            focus.focus("host");
            return;
        }

        interactor.checkHost(host.get())
                .map(HttpUrl::toString)
                .flatMap(checkedHost -> interactor.isExternalLoginFormsAllowed(checkedHost)
                        //.onErrorReturnItem(false)
                        .map(allowed -> new CheckedHost(checkedHost, allowed))
                )
                .compose(subscriber::defaultSchedulers)
                .doOnSubscribe(disposable -> isInProgress.set(true))
                .doFinally(() -> isInProgress.set(false))
                .doOnError(error -> {
                    hostError.set(appResources.getString(R.string.error_unrechable_host));
                    focus.focus("host");
                })
                .compose(globalBag::track)
                .subscribe(subscriber.subscribe(checkedHost -> {

                    handleCheckedHostAndSetStateToLogin(checkedHost);

                    interactor.storeLastInputedHost(checkedHost.host)
                            .onErrorResumeNext(error -> interactor.storeLastInputedHost(null))
                            .onErrorComplete()
                            .compose(subscriber::defaultSchedulers)
                            .subscribe(subscriber.justSubscribe());

                }));
    }

    public boolean onHostEditorEvent(EditorEvent event) {

        if (event.getActionId() == EditorInfo.IME_ACTION_NEXT
                || event.getKeyEvent() != null
                && event.getKeyEvent().getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            onHostWritten();

        }

        return true;

    }

    public void onLogin() {

        if (isInProgress.get()) return;

        if (TextUtils.isEmpty(login.get())) {
            loginError.set(appResources.getString(R.string.error_field_required));
            focus.focus("login");
            return;
        }

        if (TextUtils.isEmpty(password.get())) {
            passwordError.set(appResources.getString(R.string.error_field_required));
            focus.focus("password");
            return;
        }

        interactor.login(checkedHost, login.get(), password.get())
                .compose(subscriber::defaultSchedulers)
                .doOnSubscribe(dis -> isInProgress.set(true))
                .doFinally(() -> isInProgress.set(false))
                .doOnError(error -> {
                    if (error instanceof AuthError) {
                        loginError.set(appResources.getString(R.string.error_pass_or_login));
                        passwordError.set(appResources.getString(R.string.error_pass_or_login));
                        focus.focus("password");
                    }
                })
                .compose(globalBag::track)
                .subscribe(subscriber.subscribe(this::handleAuthResult));

    }

    public boolean onWebViewTouch() {

        if (isInProgress.get()) {
            return true;
        }

        // Do small delay for correct touch event handling
        Completable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> loginWebViewFullscreen.set(true));

        return false;
    }

    public void onPageLoadingStarted(String url) {

        MyLog.d("Start load url: " + url);

        switch (webViewState.get()) {

            case ERROR:
                pageReloading.set(true);
                break;

            case NORMAL:
                webViewState.set(PROGRESS);
                break;

            default:
                //no-op

        }

    }

    public void onPageLoadingFinished(String url) {

        MyLog.d("Loaded url: " + url);

        if (isPageLoadingAfterCancellAuth) {

            isPageLoadingAfterCancellAuth = false;
            webViewClearHistoryCommand.fire();

        }

        if (webViewState.get() == ERROR && !errorStateHandled) return;

        webViewState.set(networkAvailable ? NORMAL : ERROR);
        pageReloading.set(false);

    }

    public boolean shouldOverrideUrlLoading(String url) {

        if (!networkAvailable) {

            moveToWebErrorState();

            return true;

        }

        String urlWithoutScheme = urlWithoutScheme(url);
        String checkedHostWithoutScheme = urlWithoutScheme(checkedHost);

        if (!urlWithoutScheme.equals(checkedHostWithoutScheme)) return false;

        String cookie = CookieManager.getInstance().getCookie(url);
        parseAuthorizedCookie(cookie);

        return true;

    }

    public void onBackPressed() {

        if (viewModelState.get() == LOGIN) {

            if (loginWebViewFullscreen.get()) {

                if (isPageLoadingAfterCancellAuth) {

                    loginWebViewFullscreen.set(false);

                } else {

                    webViewGoBackCommand.goBack(success -> {

                        if (!success) {
                            loginWebViewFullscreen.set(false);
                        }

                    });

                }

            } else {

                if (!relogin) {
                    viewModelState.set(HOST);
                    webViewState.set(NOT_AVAILABLE);
                }

            }

        } else {

            authorizationResolver.onAuthorizationFailed();
            router.finishChain();

        }

    }

    public void onWebViewError(@SuppressWarnings("unused") int errorCode) {

        uiHandler.post(this::moveToWebErrorState);

    }

    public void onRetryWeb() {

        errorStateHandled = true;

        if (pageReloading.get()) {
            return;
        }

        reloadWebViewCommand.fire();
    }

    @Override
    protected void onCleared() {
        globalBag.dispose();
        super.onCleared();
    }

    private void initProperties() {

        SimpleOnPropertyChangedCallback.addTo(host, () -> hostError.set(null));
        SimpleOnPropertyChangedCallback.addTo(login, () -> {
            loginError.set(null);
            if (!TextUtils.isEmpty(login.get())) {
                passwordError.set(null);
            }
        });
        SimpleOnPropertyChangedCallback.addTo(password, () -> {
            passwordError.set(null);
            if (!TextUtils.isEmpty(password.get())) {
                loginError.set(null);
            }
        });

    }

    private void initObservables() {

        Single.zip(
                reloginPublisher
                        .firstOrError(),
                interactor.getLastInputedHost()
                        .toSingle("")
                        .compose(subscriber::defaultSchedulers),
                LastLoggedHost::new
        )//--->
                .subscribe(subscriber.subscribe(this::handleLastCheckedHostUrl));

        interactor.listenNetworkState()
                .compose(subscriber::defaultSchedulers)
                .compose(globalBag::track)
                .subscribe(subscriber.subscribe(available -> networkAvailable = available));

    }

    private void handleLastCheckedHostUrl(LastLoggedHost lastHost) {

        if (!TextUtils.isEmpty(lastHost.host)) {

            host.set(lastHost.host);

            if (lastHost.relogin) {

                viewModelState.set(LOGIN_INITIALIZATION);

                Single.zip(
                        interactor.isExternalLoginFormsAllowed(lastHost.host),
                        Single.timer(1, TimeUnit.SECONDS),
                        (allowed, tick) -> allowed
                )//--->
                        .onErrorReturnItem(false)
                        .map(allowed -> new CheckedHost(lastHost.host, allowed))
                        .compose(subscriber::defaultSchedulers)
                        .subscribe(subscriber.subscribe(
                                this::handleCheckedHostAndSetStateToLogin,
                                error -> {
                                    viewModelState.set(HOST);
                                    return true;
                                }
                        ));

            }
        }

    }

    private void handleCheckedHostAndSetStateToLogin(CheckedHost checkedHost) {

        this.checkedHost = checkedHost.host;

        if (checkedHost.externalLoginAllowed) {

            loginUrl.set(getLoginUrl(checkedHost.host));
            webViewState.set(INITIALIZATION);
            focus.focus("login");

        } else {

            webViewState.set(NOT_AVAILABLE);

        }

        viewModelState.set(LOGIN);

    }

    private void moveToWebErrorState() {

        errorStateHandled = false;
        webViewStopLoadingCommand.fire();
        webViewState.set(ERROR);
        pageReloading.set(false);

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

                isPageLoadingAfterCancellAuth = true;
                webViewClearHistoryCommand.fire();
                loginUrl.set(getLoginUrl(checkedHost));

                break;

        }
    }

    private String getLoginUrl(String host) {

        if (host == null) return null;

        return host + "?external-clients-login-form"
                        + "&locale=" + appResources.getString(R.string.value_webView_localization);
    }

    private String urlWithoutScheme(String url) {
        return url.startsWith("http://") ? url.substring(7) : url.substring(8);
    }

}
