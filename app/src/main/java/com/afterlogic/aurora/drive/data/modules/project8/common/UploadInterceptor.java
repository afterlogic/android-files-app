package com.afterlogic.aurora.drive.data.modules.project8.common;

import android.text.TextUtils;

import com.afterlogic.aurora.drive.core.util.NumberUtil;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by sashka on 28.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class UploadInterceptor implements Interceptor {

    public static final String QUERY_PATH = "uploadPath";
    public static final String QUERY_TYPE = "uploadType";
    public static final String QUERY_INTERCEPT= "interceptUpload";
    public static final String INTERCEPT_UPLOAD = "?" + QUERY_INTERCEPT + "=true";

    private static final String UPLOAD_QUERY = "upload/files/%s%s";

    public UploadInterceptor() {

    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpUrl url = request.url();

        boolean interceptUpload = NumberUtil.parseBoolean(url.queryParameter(QUERY_INTERCEPT), false);

        if (interceptUpload) {

            String uploadPath = url.queryParameter(QUERY_PATH);
            String uploadType = url.queryParameter(QUERY_TYPE);

            if (!TextUtils.isEmpty(uploadPath) && !TextUtils.isEmpty(uploadType)) {

                url = request.url().newBuilder()
                        .removeAllQueryParameters(QUERY_PATH)
                        .removeAllQueryParameters(QUERY_TYPE)
                        .removeAllQueryParameters(QUERY_INTERCEPT)
                        .rawEncodedQuery(String.format(UPLOAD_QUERY, uploadType, uploadPath))
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
