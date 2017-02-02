package com.afterlogic.aurora.drive._unrefactored.data.modules.project8.common;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive._unrefactored.data.common.SessionManager;
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
        Map<String, Object> fields = super.getDefaultFields(method, params);
        fields.put(Api8.Field.AUTH_TOKEN, mSessionManager.getAuroraSession().getAuthToken());
        return fields;
    }

    protected Map<String, Object> getDefaultFields(@NonNull String method, Map<String, ?> params){
        Map<String, Object> fields = super.getDefaultFields(method, params);
        fields.put(Api8.Field.AUTH_TOKEN, mSessionManager.getAuroraSession().getAuthToken());
        return fields;
    }

    public SessionManager getSessionManager() {
        return mSessionManager;
    }
}