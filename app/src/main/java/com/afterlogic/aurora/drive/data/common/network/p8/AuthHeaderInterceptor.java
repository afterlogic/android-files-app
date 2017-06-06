package com.afterlogic.aurora.drive.data.common.network.p8;

import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.AuroraSession;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by aleksandrcikin on 06.06.17.
 * mail: mail@sunnydaydev.me
 */

class AuthHeaderInterceptor implements Interceptor {

    private final SessionManager mSessionManager;

    @Inject
    public AuthHeaderInterceptor(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        AuroraSession session = mSessionManager.getSession();

        Request request;
        if (session != null) {
            String auth = Api8.Header.AUTH_TOKEN_PREFIX + " " + session.getAuthToken();
            request = chain.request().newBuilder()
                    .addHeader(Api8.Header.AUTH_TOKEN, auth)
                    .build();
        } else {
            request = chain.request();
        }
        return chain.proceed(request);
    }
}
