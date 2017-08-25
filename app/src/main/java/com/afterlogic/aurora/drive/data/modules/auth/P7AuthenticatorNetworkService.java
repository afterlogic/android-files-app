package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.data.common.network.util.ApiUtil;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

class P7AuthenticatorNetworkService {

    private final Api7 api;

    @Inject
    P7AuthenticatorNetworkService(Api7 api) {
        this.api = api;
    }

    public Single<SystemAppData> getSystemAppData(String host) {
        return getSystemAppData(host, null);
    }

    public Single<SystemAppData> getSystemAppData(String host, String authToken) {
        return Single.defer(() -> {

            HashMap<String, Object> fields = new HashMap<>();

            fields.put(Api7.Fields.ACTION, Api7.Actions.SYSTEM_GET_APP_DATA);
            if (authToken != null) {
                fields.put(Api7.Fields.AUTH_TOKEN, authToken);
            }

            return api.getSystemAppData(completeUrl(host), fields);

        })//--->
                .compose(ApiUtil::checkResponseAndGetData);
    }

    public Single<AuthToken> login(String host, String login, String pass) {
        return Single.defer(() -> {

            HashMap<String, Object> fields = new HashMap<>();

            fields.put(Api7.Fields.ACTION, Api7.Actions.SYSTEM_LOGIN);
            fields.put(Api7.Fields.EMAIL, login);
            fields.put(Api7.Fields.INC_PASSWORD, pass);

            return api.login(completeUrl(host), fields);

        })//--->
                .compose(ApiUtil::checkResponse)
                .map(response -> {
                    AuthToken token = response.getResult();
                    token.userId = response.getAccountId();
                    return token;
                });
    }

    private String completeUrl(String base) {
        return base + Api7.AJAX;
    }
}
