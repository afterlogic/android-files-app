package com.afterlogic.aurora.drive.presentation.modules.login.interactor;

import android.accounts.Account;

import com.afterlogic.aurora.drive.core.common.contextWrappers.AccountHelper;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.auth.AuthenticatorService;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by aleksandrcikin on 22.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginInteractor {

    private final AppPrefs prefs;
    private final AuthenticatorService authenticatorService;
    private final SessionManager sessionManager;
    private final AccountHelper accountHelper;

    @Inject
    public LoginInteractor(AppPrefs prefs,
                           AuthenticatorService authenticatorService,
                           SessionManager sessionManager,
                           AccountHelper accountHelper) {

        this.prefs = prefs;
        this.authenticatorService = authenticatorService;
        this.sessionManager = sessionManager;
        this.accountHelper = accountHelper;
    }

    public Maybe<String> getLastInputedHost() {
        return Maybe.defer(() -> {
            String lastHost = prefs.lastInputedHost().get();
            if (lastHost == null) {
                return Maybe.empty();
            } else {
                return Maybe.just(lastHost);
            }
        });
    }

    public Completable storeLastInputedHost(String host) {
        return Completable.fromAction(() -> prefs.lastInputedHost().set(host));
    }

    public Single<HttpUrl> checkHost(String host) {
        return Single.defer(() -> {

            boolean manual;

            HttpUrl domain;
            if (host.contains("://")) {
                manual = true;
                domain = HttpUrl.parse(host);
            } else {
                manual = false;
                domain = HttpUrl.parse("http://" + host);
            }

            if (domain == null) {
                return Single.error(new Error("Error while parse host"));
            }

            List<HttpUrl> domains = new ArrayList<>();

            if (manual){
                domains.add(domain);
            } else {
                domains.add(domain.newBuilder().scheme("https").build());
                domains.add(domain.newBuilder().scheme("http").build());
            }

            return Stream.of(domains)
                    .map(domainItem ->
                            authenticatorService.getApiVersion(domainItem.toString())
                                    .toObservable()
                                    .map(version -> domainItem)
                    )
                    .collect(Observables.Collectors.concatObservables())
                    .firstElement()
                    .switchIfEmpty(Maybe.error(new UnknownApiVersionError()))
                    .toSingle();
        });
    }

    public Completable handleAuth(String authToken, HttpUrl host) {
        return authenticatorService.createSession(host.toString(), authToken)
                .flatMap(session -> {

                    Account currentAccount = accountHelper.getCurrentAccount();

                    if (currentAccount == null) {

                        accountHelper.createAccount(session.getLogin());
                        return Single.just(session);

                    } else if (currentAccount.name.equals(session.getLogin())){

                        return Single.just(session);

                    } else {

                        return Single.error(new Error("TODO: Handle login changing"));

                    }

                })
                .doOnSuccess(sessionManager::setSession)
                .toCompletable();

    }
}
