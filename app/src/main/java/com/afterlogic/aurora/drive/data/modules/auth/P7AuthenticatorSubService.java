package com.afterlogic.aurora.drive.data.modules.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.model.AuthorizedAuroraSession;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.annimon.stream.Stream;
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
                .map(loginResult -> handleLoginResult(loginResult, host, email, pass));
    }

    @Override
    public Single<AuthorizedAuroraSession> byToken(String host, String token) {
        return service.getSystemAppData(host, token)
                .map(authPair -> new LoginResult(token, authPair))
                .map(loginResult -> handleLoginResult(loginResult, host, null, null));
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

    private AuthorizedAuroraSession handleLoginResult(@NonNull LoginResult loginResult,
                                                      @NonNull String host,
                                                      @Nullable String email,
                                                      @Nullable String pass) {

        SystemAppData systemAppData = loginResult.getSystemAppData();

        if (systemAppData.isAuthorized()) {
            throw new Error("Not authorized!");
        }

        long defaultAccountId = systemAppData.getDefault();

        SystemAppData.Account defaultAccount = Stream.of(systemAppData.getAccounts())
                .filter(acc -> acc.getAccountID() == defaultAccountId)
                .findFirst()
                .get();

        return new AuthorizedAuroraSession(
                defaultAccount.getEmail(),
                loginResult.getSystemAppData().getToken(),
                loginResult.getToken(),
                loginResult.getAccountId(), // TODO: Check is really need it
                email,
                pass,
                HttpUrl.parse(host),
                Const.ApiVersion.API_P7
        );

    }

    @SuppressWarnings("WeakerAccess")
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
        return error instanceof JsonSyntaxException || error instanceof ApiResponseError;

    }
}
