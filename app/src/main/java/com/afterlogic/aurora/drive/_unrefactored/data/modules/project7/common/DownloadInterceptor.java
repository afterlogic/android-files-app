package com.afterlogic.aurora.drive._unrefactored.data.modules.project7.common;

import android.text.TextUtils;

import com.afterlogic.aurora.drive._unrefactored.core.util.NumberUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by sashka on 28.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class DownloadInterceptor implements Interceptor {

    static final String QUERY_ACCOUNT_ID = "accountId";
    static final String QUERY_HASH = "hash";
    static final String QUERY_AUTH_TOKEN = "authToken";
    private static final String QUERY_INTERCEPT= "interceptDownload";
    static final String INTERCEPT_DOWNLOAD = "?" + QUERY_INTERCEPT + "=true";
    private static final String LINK = Api7.Links.FILE_DOWNLOAD_LINK.replace("?", "");

    public DownloadInterceptor() {

    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpUrl url = request.url();

        boolean interceptUpload = NumberUtil.parseBoolean(url.queryParameter(QUERY_INTERCEPT), false);

        if (interceptUpload) {

            String hash = url.queryParameter(QUERY_HASH);
            String token = url.queryParameter(QUERY_AUTH_TOKEN);
            long accountId = NumberUtil.parseLong(url.queryParameter(QUERY_ACCOUNT_ID), -1);

            if (!TextUtils.isEmpty(hash) && !TextUtils.isEmpty(token) && accountId != -1) {

                url = request.url().newBuilder()
                        .removeAllQueryParameters(QUERY_HASH)
                        .removeAllQueryParameters(QUERY_AUTH_TOKEN)
                        .removeAllQueryParameters(QUERY_INTERCEPT)
                        .removeAllQueryParameters(QUERY_ACCOUNT_ID)
                        .rawEncodedQuery(String.format(Locale.US, LINK, accountId, hash, token))
                        .build();

                request = request.newBuilder()
                        .url(url)
                        .build();
            } else {
                throw new IOException("Intercepted upload params non complete!");
            }
        }
        return chain.proceed(request);
    }
}
