package com.afterlogic.aurora.drive.data.modules.auth;

import androidx.core.util.Pair;

import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.model.project8.UserP8;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.AuthorizedAuroraSession;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
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
                        auth.getUser().getName(),
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
                .map(userData -> {

                    UserP8 user = userData.second;
                    Long id = userData.first;

                    if (user == null || id == null) {
                        throw new IllegalArgumentException("User and id must be not null.");
                    }

                    return new AuthorizedAuroraSession(
                            user.getName(),
                            "APP_TOKEN_STUB",
                            token,
                            id,
                            null,
                            null,
                            HttpUrl.parse(host),
                            Const.ApiVersion.API_P8
                    );

                });

    }

    @Override
    public Single<Boolean> isExternalClientLoginFormsAvailable(String host) {

        return service.checkExternalLoginFormsAvailable(host)
                .onErrorResumeNext(error -> {

                    if (error instanceof ApiResponseError) {

                        ApiResponseError apiError = (ApiResponseError) error;

                        if (apiError.getErrorCode() == ApiResponseError.MODULE_NOT_EXIST
                                || apiError.getErrorCode() == ApiResponseError.METHOD_NOT_EXIST) {

                            return Single.just(false);

                        }

                    }

                    return Single.error(error);

                });

    }

    @Override
    public Maybe<Integer> getApiVersion(String host) {

        return service.ping(host)
                .map(pong -> true)
                .onErrorResumeNext(error -> isIncorrectApiVersionError(error) ?
                        Single.just(false): Single.error(error)
                )
                .toMaybe()
                .flatMap(isP8 -> isP8 ? Maybe.just(Const.ApiVersion.API_P8) : Maybe.empty());

    }

    private boolean isIncorrectApiVersionError(Throwable error) {
        return error instanceof JsonSyntaxException;
    }

    private class AuthorizedData {

        private final String token;
        private final UserP8 user;
        private final Long accountId;

        private AuthorizedData(AuthToken authToken, Pair<Long, UserP8> user) {
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
