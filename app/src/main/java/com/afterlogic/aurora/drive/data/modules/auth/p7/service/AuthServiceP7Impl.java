package com.afterlogic.aurora.drive.data.modules.auth.p7.service;

import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.data.common.network.p7.AuthorizedServiceP7;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuthServiceP7Impl extends AuthorizedServiceP7 implements AuthServiceP7 {

    private final Api7 mApi;

    @Inject AuthServiceP7Impl(Api7 api, SessionManager sessionManager) {
        super(sessionManager);
        mApi = api;
    }

    @Override
    public Single<ApiResponseP7<AuthToken>> login(String login, String pass) {
        return Single.defer(() -> {
            HashMap<String, Object> fiels = new HashMap<>();
            fiels.put(Api7.Fields.ACTION, Api7.Actions.SYSTEM_LOGIN);
            fiels.put(Api7.Fields.EMAIL, login);
            fiels.put(Api7.Fields.INC_PASSWORD, pass);
            AuroraSession session = getSessionManager().getSession();
            if (session != null) {
                fiels.put(Api7.Fields.TOKEN, session.getAppToken());
            }
            return mApi.login(fiels);
        });
    }

    @Override
    public Single<ApiResponseP7<SystemAppData>> getSystemAppData() {
        return Single.defer(() -> {
            HashMap<String, Object> fields = new HashMap<>();
            fields.put(Api7.Fields.ACTION, Api7.Actions.SYSTEM_GET_APP_DATA);
            return mApi.getSystemAppData(fields);
        });
    }
}
