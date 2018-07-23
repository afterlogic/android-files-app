package com.afterlogic.aurora.drive.data.modules.auth;

import androidx.core.util.Pair;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.data.common.network.util.ApiUtil;
import com.afterlogic.aurora.drive.data.model.project8.GetUserParametersDto;
import com.afterlogic.aurora.drive.data.model.project8.LoginParametersDto;
import com.afterlogic.aurora.drive.data.model.project8.UserP8;
import com.afterlogic.aurora.drive.model.AuthToken;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

class P8AuthenticatorNetworkService {

    private final Api8 api;

    @Inject
    P8AuthenticatorNetworkService(Api8 api) {
        this.api = api;
    }

    Single<String> ping(String host){
        return api.ping(Api8.completeUrl(host))
                .compose(ApiUtil::checkResponseAndGetData);
    }

    Single<Pair<Long, UserP8>> getUser(String host, String token) {
        return api.getUser(Api8.completeUrl(host), token, new GetUserParametersDto(token))
                .compose(ApiUtil::checkResponse)
                .map(response -> new Pair<>(response.getAccountId(), response.getResult()));
    }

    Single<AuthToken> login(String host, String login, String password) {
        return api.login(Api8.completeUrl(host), new LoginParametersDto(login, password))
                .compose(ApiUtil::checkResponseAndGetData);
    }

    Single<Boolean> checkExternalLoginFormsAvailable(String host) {
        return api.checkExternalClientLoginFormAvailable(Api8.completeUrl(host))
                .compose(ApiUtil::checkResponseAndGetData);
    }

}
