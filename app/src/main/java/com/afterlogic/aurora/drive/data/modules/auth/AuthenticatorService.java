package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

@DataScope
public class AuthenticatorService {

    private final SessionManager sessionManager;
    private final AuthenticatorSubService p7AuthenticatorSubService;
    private final AuthenticatorSubService p8AuthenticatorSubService;

    @Inject
    AuthenticatorService(SessionManager sessionManager,
                         P7AuthenticatorSubService p7AuthenticatorService,
                         P8AuthenticatorSubService p8AuthenticatorService) {

        this.sessionManager = sessionManager;
        this.p7AuthenticatorSubService = p7AuthenticatorService;
        this.p8AuthenticatorSubService = p8AuthenticatorService;
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

    public Single<Integer> getApiVersion(String host) {
        return Observable.concat(
                p8AuthenticatorSubService.getApiVersion(host)
                        .toObservable(),
                p7AuthenticatorSubService.getApiVersion(host)
                        .toObservable()
        )//--->
                .switchIfEmpty(Observable.error(new UnknownApiVersionError()))
                .firstOrError();
    }

    private Single<AuthenticatorSubService> getAuthenticatorService(String host) {
        return getApiVersion(host)
                .map(version -> version == Const.ApiVersion.API_P7
                        ? p7AuthenticatorSubService : p8AuthenticatorSubService);
    }
}
