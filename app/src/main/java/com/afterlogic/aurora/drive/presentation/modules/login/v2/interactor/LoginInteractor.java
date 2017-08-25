package com.afterlogic.aurora.drive.presentation.modules.login.v2.interactor;

import android.support.v4.util.Pair;

import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.util.Lazy;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiChecker;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by aleksandrcikin on 22.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginInteractor {

    private final ApiChecker apiChecker;
    private final Provider<AuthRepository> authRepositoryProvider;
    private final SessionManager sessionManager;
    private final AppPrefs prefs;

    @Inject
    public LoginInteractor(ApiChecker apiChecker,
                           Provider<AuthRepository> authRepositoryProvider,
                           SessionManager sessionManager,
                           AppPrefs prefs) {

        this.apiChecker = apiChecker;
        this.authRepositoryProvider = authRepositoryProvider;
        this.sessionManager = sessionManager;
        this.prefs = prefs;
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
                    .map(domainItem -> apiChecker.getApiVersion(domainItem)
                            .map(version -> new Pair<>(domainItem, version))
                            .toObservable()
                    )
                    .collect(Observables.Collectors.concatObservables())
                    .filter(versionPair -> versionPair.second != Const.ApiVersion.API_NONE)
                    .firstElement()
                    .map(versionPair -> versionPair.first)
                    .switchIfEmpty(Maybe.error(new UnknownApiVersionError()))
                    .toSingle();
        });
    }

    public Completable handleAuth(String authToken, HttpUrl host) {

        AuroraSession session = new AuroraSession(
                null,
                authToken,
                -1,
                "unknown",
                null,
                host,
                Const.ApiVersion.API_NONE
        );

        Lazy<AuthRepository> lazyAuth = new Lazy<>(authRepositoryProvider::get);

        return apiChecker.getApiVersion(host)
                .flatMap(version -> {
                    session.setApiType(version);
                    sessionManager.setSession(session);

                    return lazyAuth.get().getSystemAppData();
                })
                .map(SystemAppData::getToken)
                .flatMapCompletable(appToken -> {
                    session.setAppToken(appToken);
                    return lazyAuth.get().setCurrentSession(session);
                });
    }
}
