package com.afterlogic.aurora.drive.data.common.network.p8;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuthorizedServiceP8 extends CloudServiceP8{

    private final SessionManager mSessionManager;

    public AuthorizedServiceP8(String moduleName, SessionManager sessionManager, Gson gson) {
        super(moduleName, gson);
        mSessionManager = sessionManager;
    }

    @Override
    protected Map<String, Object> getDefaultFields(@NonNull String method, @NonNull String params) {
        AuroraSession session = mSessionManager.getSession();
        Map<String, Object> fields = super.getDefaultFields(method, params);
        fields.put(Api8.Field.AUTH_TOKEN, session.getAuthToken());
        return fields;
    }

    protected Map<String, Object> getDefaultFields(@NonNull String method, Map<String, ?> params){
        AuroraSession session = mSessionManager.getSession();
        Map<String, Object> fields = super.getDefaultFields(method, params);
        fields.put(Api8.Field.AUTH_TOKEN, session.getAuthToken());
        return fields;
    }

    public SessionManager getSessionManager() {
        return mSessionManager;
    }
}
