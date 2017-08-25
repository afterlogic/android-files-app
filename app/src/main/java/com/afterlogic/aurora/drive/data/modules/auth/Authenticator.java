package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

@DataScope
public class Authenticator {

    private final SessionManager sessionManager;
    private final AuthenticatorService p7AuthenticatorService;
    private final AuthenticatorService p8AuthenticatorService;

    @Inject
    Authenticator(SessionManager sessionManager,
                  P7AuthenticatorService p7AuthenticatorService,
                  P8AuthenticatorService p8AuthenticatorService) {

        this.sessionManager = sessionManager;
        this.p7AuthenticatorService = p7AuthenticatorService;
        this.p8AuthenticatorService = p8AuthenticatorService;
    }

    public Completable login(String host, String login, String pass) {
        return getAuthenticatorService(host)
                .flatMap(service -> service.login(host, login, pass))
                .doOnSuccess(sessionManager::setSession)
                .toCompletable();
    }

    public Completable updateSessionByToken(String host, String token) {
        return getAuthenticatorService(host)
                .flatMap(service -> service.byToken(host, token))
                .doOnSuccess(sessionManager::setSession)
                .toCompletable();
    }

    private Single<AuthenticatorService> getAuthenticatorService(String host) {
        return Observable.concat(p7AuthenticatorService.getApiVersion(host)
                        .onErrorComplete()
                        .toObservable(),
                p8AuthenticatorService.getApiVersion(host)
                        .onErrorComplete()
                        .toObservable()
        )//--->
                .firstOrError()
                .map(version -> version == Const.ApiVersion.API_P7
                        ? p7AuthenticatorService : p8AuthenticatorService);
    }
}
