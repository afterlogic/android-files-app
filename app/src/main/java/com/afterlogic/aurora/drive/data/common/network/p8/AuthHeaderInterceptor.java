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

        Request originalRequest = chain.request();

        if (originalRequest.headers().get(Api8.Header.NAME_AUTHORISATION) != null) {

            Request.Builder request = chain.request().newBuilder();

            request.removeHeader(Api8.Header.NAME_AUTHORISATION);

            if (session != null) {
                String token = session.getAuthToken();
                if (token != null) {
                    request.addHeader(
                            Api8.Header.NAME_AUTHORISATION,
                            Api8.Header.VALUE_AUTHORISATION.replace(Api8.Header.AUTH_TOKEN, token)
                    );
                }
            }
            return chain.proceed(request.build());

        } else {
            return chain.proceed(originalRequest);
        }
    }
}
