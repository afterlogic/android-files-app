package com.afterlogic.aurora.drive.data.common.network.p7;

import android.text.TextUtils;

import com.afterlogic.aurora.drive.core.common.util.NumberUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by sashka on 28.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class UploadInterceptor implements Interceptor {

    static final String QUERY_TYPE = "type";
    static final String QUERY_PATH = "path";
    static final String QUERY_FILENAME = "fileName";
    private static final String QUERY_INTERCEPT= "interceptUpload";
    static final String INTERCEPT_UPLOAD = "index.php?" + QUERY_INTERCEPT + "=true";

    public UploadInterceptor() {

    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpUrl url = request.url();

        String interceptUploadQuery = url.queryParameter(QUERY_INTERCEPT);
        boolean interceptUpload =interceptUploadQuery != null
                && NumberUtil.parseBoolean(interceptUploadQuery, false);

        if (interceptUpload) {

            String type = url.queryParameter(QUERY_TYPE);
            String path = url.queryParameter(QUERY_PATH);
            String fileName = url.queryParameter(QUERY_FILENAME);

            if (!TextUtils.isEmpty(type) && path != null && !TextUtils.isEmpty(fileName)) {

                url = request.url().newBuilder()
                        .removeAllQueryParameters(QUERY_TYPE)
                        .removeAllQueryParameters(QUERY_PATH)
                        .removeAllQueryParameters(QUERY_FILENAME)
                        .removeAllQueryParameters(QUERY_INTERCEPT)
                        .query(String.format(Locale.US,
                                Api7.Links.UPLOAD_FILE_URL, type, path, fileName
                        ))
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
