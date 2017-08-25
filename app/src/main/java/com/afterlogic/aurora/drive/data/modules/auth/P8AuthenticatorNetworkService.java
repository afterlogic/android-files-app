package com.afterlogic.aurora.drive.data.modules.auth;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.ParamsBuilder;
import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.data.common.network.p8.CloudServiceP8;
import com.afterlogic.aurora.drive.data.common.network.util.ApiUtil;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

class P8AuthenticatorNetworkService extends CloudServiceP8 {

    private final Api8 api;

    @Inject
    P8AuthenticatorNetworkService(@P8 @NonNull Gson gson, Api8 api) {
        super(Api8.Module.CORE, gson);
        this.api = api;
    }

    public Single<String> ping(String host){
        return Single.defer(() -> {

            Map<String, Object> fields = getDefaultFields(Api8.Method.PING, Collections.emptyMap());
            return api.ping(completeUrl(host), fields);

        })//--->
                .compose(ApiUtil::checkResponseAndGetData);
    }

    public Single<AuthToken> login(String host, String login, String password) {
        return Single.defer(() -> {
            Map<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.LOGIN, login)
                    .put(Api8.Param.PASSWORD, password)
                    .put(Api8.Param.SIGN_ME, false)
                    .create();

            Map<String, Object> fields = getDefaultFields(Api8.Method.LOGIN, params);
            return api.login(host, fields);
        })//--->
                .compose(ApiUtil::checkResponseAndGetData);
    }

    private String completeUrl(String base) {
        return base + Api8.API;
    }
}
