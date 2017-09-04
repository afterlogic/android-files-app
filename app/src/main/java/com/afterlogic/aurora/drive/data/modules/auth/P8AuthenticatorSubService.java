package com.afterlogic.aurora.drive.data.modules.auth;

import android.support.v4.util.Pair;

import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.model.project8.UserP8;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.AuthorizedAuroraSession;
import com.google.gson.JsonSyntaxException;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

class P8AuthenticatorSubService implements AuthenticatorSubService {

    private final P8AuthenticatorNetworkService service;

    @Inject
    P8AuthenticatorSubService(P8AuthenticatorNetworkService service) {
        this.service = service;
    }

    @Override
    public Single<AuthorizedAuroraSession> login(String host, String email, String pass) {
        return service.login(host, email, pass)
                .flatMap(authToken -> service.getUser(host, authToken.token)
                        .map(userInfo -> new AuthorizedData(authToken, userInfo))
                )
                .map(auth -> new AuthorizedAuroraSession(
                        auth.getUser().getPublicId(),
                        "APP_TOKEN_STUB",
                        auth.getToken(),
                        auth.getAccountId(),
                        email,
                        pass,
                        HttpUrl.parse(host),
                        Const.ApiVersion.API_P8
                ));
    }

    @Override
    public Single<AuthorizedAuroraSession> byToken(String host, String token) {
        return service.getUser(host, token)
                .map(userData -> new AuthorizedAuroraSession(
                        userData.second.getPublicId(),
                        "APP_TOKEN_STUB",
                        token,
                        userData.first,
                        null,
                        null,
                        HttpUrl.parse(host),
                        Const.ApiVersion.API_P8
                ));
    }

    @Override
    public Maybe<Integer> getApiVersion(String host) {
        return service.ping(host)
                .toMaybe()
                .onErrorResumeNext(error -> isIncorrectApiVersionError(error)
                        ? Maybe.empty() : Maybe.error(error)
                )
                .map(systemAppData -> Const.ApiVersion.API_P8);
    }

    private boolean isIncorrectApiVersionError(Throwable error) {
        return error instanceof JsonSyntaxException;
    }

    private class AuthorizedData {

        private final String token;
        private final UserP8 user;
        private final Long accountId;

        public AuthorizedData(AuthToken authToken, Pair<Long, UserP8> user) {
            this.token = authToken.token;
            this.user = user.second;
            this.accountId = user.first;
        }

        public String getToken() {
            return token;
        }

        public UserP8 getUser() {
            return user;
        }

        public Long getAccountId() {
            return accountId;
        }
    }

}
