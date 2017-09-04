package com.afterlogic.aurora.drive.data.modules.auth;

import android.support.v4.util.Pair;

import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.model.AuthorizedAuroraSession;
import com.afterlogic.aurora.drive.model.SystemAppData;
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

class P7AuthenticatorSubService implements AuthenticatorSubService {

    private final P7AuthenticatorNetworkService service;

    @Inject
    P7AuthenticatorSubService(P7AuthenticatorNetworkService service) {
        this.service = service;
    }

    @Override
    public Single<AuthorizedAuroraSession> login(String host, String email, String pass) {
        return service.login(host, email, pass)
                .flatMap(authToken -> service.getSystemAppData(host, authToken.token)
                        .map(systemAppData -> new LoginResult(authToken.token, systemAppData))
                )
                .map(loginResult -> new AuthorizedAuroraSession(
                        String.valueOf(loginResult.getSystemAppData().getUser().getIdUser()),
                        loginResult.getSystemAppData().getToken(),
                        loginResult.getToken(),
                        0, // TODO: Check is really need it
                        email,
                        pass,
                        HttpUrl.parse(host),
                        Const.ApiVersion.API_P7
                ));
    }

    @Override
    public Single<AuthorizedAuroraSession> byToken(String host, String token) {
        return service.getSystemAppData(host, token)
                .map(authPair -> {

                    SystemAppData systemAppData = authPair.second;

                    if (systemAppData.isAuthorized()) {
                        throw new Error("Not authorized!");
                    }

                    SystemAppData.User authenticatedUser = systemAppData.getUser();

                    return new AuthorizedAuroraSession(
                            String.valueOf(authenticatedUser.getIdUser()),
                            systemAppData.getToken(),
                            token,
                            authPair.first, // TODO: Check is really need it
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
                .onErrorResumeNext(error -> isIncorrectApiVersionError(error)
                        ? Maybe.empty() : Maybe.error(error)
                )
                .map(systemAppData -> Const.ApiVersion.API_P7);
    }

    private class LoginResult {

        private long accountId;
        private String token;
        private SystemAppData systemAppData;

        public LoginResult(String token, Pair<Long, SystemAppData> systemAppDataPair) {
            this.token = token;
            this.systemAppData = systemAppDataPair.second;
            this.accountId = systemAppDataPair.first;
        }

        public String getToken() {
            return token;
        }

        public SystemAppData getSystemAppData() {
            return systemAppData;
        }

        public long getAccountId() {
            return accountId;
        }
    }

    private boolean isIncorrectApiVersionError(Throwable error) {
        if (error instanceof JsonSyntaxException) return true;
        if (error instanceof ApiResponseError) return true;

        return false;
    }
}
