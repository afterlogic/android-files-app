package com.afterlogic.aurora.drive.data.common.network.p8;

import android.support.annotation.NonNull;

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

    private final SessionManager sessionManager;

    @Inject
    AuthHeaderInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        AuroraSession session = sessionManager.getSession();

        Request originalRequest = chain.request();

        String authHeader = originalRequest.headers().get(Api8.ApiHeader.NAME_AUTHORISATION);

        if (authHeader != null && authHeader.equals(Api8.ApiHeader.VALUE_AUTHORISATION)) {

            Request.Builder request = chain.request().newBuilder();

            request.removeHeader(Api8.ApiHeader.NAME_AUTHORISATION);

            if (session != null) {

                String token = session.getAuthToken();

                if (token != null) {

                    String authHeaderValue = Api8.ApiHeader.VALUE_AUTHORISATION
                            .replace(Api8.ApiHeader.AUTH_TOKEN, token);

                    request.addHeader(
                            Api8.ApiHeader.NAME_AUTHORISATION,
                            authHeaderValue
                    );

                }

            }

            return chain.proceed(request.build());

        } else {

            return chain.proceed(originalRequest);

        }

    }

}
