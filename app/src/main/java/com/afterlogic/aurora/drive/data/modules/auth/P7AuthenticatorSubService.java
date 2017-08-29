package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.SystemAppData;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

class P7AuthenticatorSubService implements AuthenticatorSubService {

    private final P7AuthenticatorNetworkService service;

    @Inject
    P7AuthenticatorSubService(P7AuthenticatorNetworkService service) {
        this.service = service;
    }

    @Override
    public Single<AuroraSession> login(String host, String login, String pass) {
        return Single.zip(
                service.getSystemAppData(host).map(SystemAppData::getToken),
                service.login(host, login, pass),
                (appToken, authToken) -> new AuroraSession(
                        appToken,
                        authToken.token,
                        authToken.userId,
                        login,
                        pass,
                        HttpUrl.parse(host),
                        Const.ApiVersion.API_P7
                )
        );
    }

    @Override
    public Single<AuroraSession> byToken(String host, String token) {
        return service.getSystemAppData(host, token)
                .map(systemAppData -> {

                    if (systemAppData.isAuthorized()) {
                        throw new Error("Not authorized!");
                    }

                    return new AuroraSession(
                            systemAppData.getToken(),
                            token,
                            systemAppData.getUser().getIdUser(),
                            null,
                            null,
                            HttpUrl.parse(host),
                            Const.ApiVersion.API_P7
                    );
                });
    }

    @Override
    public Maybe<Integer> getApiVersion(String host) {
        return service.getSystemAppData(host)
                .toMaybe()
                .onErrorComplete() // TODO: complete only another api version else throw error
                .map(systemAppData -> Const.ApiVersion.API_P7);
    }

}
