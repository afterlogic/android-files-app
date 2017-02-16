package com.afterlogic.aurora.drive.core.common.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import okhttp3.HttpUrl;

/**
 * Created by sashka on 29.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class HttpUrlUtil {

    @Nullable
    public static HttpUrl parseCompleted(@Nullable String domain, String defaultScheme){
        if (domain == null) return null;

        HttpUrl url = HttpUrl.parse(domain);
        if (url == null){
            //may be skipped scheme
            url = HttpUrl.parse(defaultScheme + "://" + domain);
        }

        if (url != null){
            String lastPath = url.pathSegments().get(url.pathSize() - 1);
            if (!lastPath.contains(".")){
                url = url.newBuilder()
                        .addEncodedPathSegment("")
                        .build();
            }
        }

        boolean complete = url != null && TextUtils.isEmpty(url.fragment()) &&
                TextUtils.isEmpty(url.query()) &&
                "".equals(url.pathSegments().get(url.pathSize() -1));

        return complete ? url : null;
    }
}
