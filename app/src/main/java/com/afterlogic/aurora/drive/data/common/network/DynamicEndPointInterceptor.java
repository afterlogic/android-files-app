package com.afterlogic.aurora.drive.data.common.network;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class DynamicEndPointInterceptor implements Interceptor {

    public static String DYNAMIC_BASE_URL = "http://url.dynamic/";

    private DynamicDomainProvider dynamicDomainProvider;
    private String replacebleUrlPart;

    public DynamicEndPointInterceptor(String urlPartForReplace, DynamicDomainProvider dynamicDomainProvider) {
        this.dynamicDomainProvider = dynamicDomainProvider;
        replacebleUrlPart = urlPartForReplace;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String currentUrl = request.url().toString();

        if (currentUrl.contains(replacebleUrlPart)) {
            HttpUrl base = dynamicDomainProvider.getDomain();

            if (base != null) {
                request = request.newBuilder()
                        .url(currentUrl.replace(replacebleUrlPart, base.toString()))
                        .build();
            } else {
                MyLog.e(this, "Dynamic base url is null.");
            }
        }
        return chain.proceed(request);
    }
}
