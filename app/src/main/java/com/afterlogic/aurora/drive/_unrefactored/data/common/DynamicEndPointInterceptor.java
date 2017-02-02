package com.afterlogic.aurora.drive._unrefactored.data.common;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class DynamicEndPointInterceptor implements Interceptor {

    public static String DYNAMIC_BASE_URL = "http://url.dynamic/";

    private DynamicDomainProvider mDynamicDomainProvider;
    private String mReplacableUrlPart;

    public DynamicEndPointInterceptor(String urlPartForReplace, DynamicDomainProvider dynamicDomainProvider) {
        mDynamicDomainProvider = dynamicDomainProvider;
        mReplacableUrlPart = urlPartForReplace;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String currentUrl = request.url().toString();

        if (currentUrl.contains(mReplacableUrlPart)) {
            HttpUrl base = mDynamicDomainProvider.getDomain();

            if (base != null) {
                request = request.newBuilder()
                        .url(currentUrl.replace(mReplacableUrlPart, base.toString()))
                        .build();
            } else {
                MyLog.e(this, "Dynamic base url is null.");
            }
        }
        return chain.proceed(request);
    }
}
