package com.afterlogic.aurora.drive.data.common.network;

import okhttp3.HttpUrl;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface DynamicDomainProvider {
    HttpUrl getDomain();
}
