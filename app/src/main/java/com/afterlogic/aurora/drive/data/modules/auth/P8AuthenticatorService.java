package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.model.AuroraSession;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

class P8AuthenticatorService implements AuthenticatorService{

    private final P8AuthenticatorNetworkService service;

    @Inject
    P8AuthenticatorService(P8AuthenticatorNetworkService service) {
        this.service = service;
    }

    @Override
    public Single<AuroraSession> login(String host, String login, String pass) {
        return service.login(host, login, pass)
                .map(authToken -> new AuroraSession(
                        "APP_TOKEN_STUB",
                        authToken.token,
                        -1,
                        login,
                        pass,
                        HttpUrl.parse(host),
                        Const.ApiVersion.API_P8
                ));
    }

    @Override
    public Single<AuroraSession> byToken(String host, String token) {
        return service.ping(host)
                .toCompletable()
                .andThen(Single.fromCallable(() -> new AuroraSession(
                        "APP_TOKEN_STUB",
                        token,
                        -1,
                        null,
                        null,
                        HttpUrl.parse(host),
                        Const.ApiVersion.API_P8
                )));
    }

    @Override
    public Maybe<Integer> getApiVersion(String host) {
        return service.ping(host)
                .toMaybe()
                .onErrorComplete() // TODO: complete only another api version else throw error
                .map(systemAppData -> Const.ApiVersion.API_P7);
    }

}
