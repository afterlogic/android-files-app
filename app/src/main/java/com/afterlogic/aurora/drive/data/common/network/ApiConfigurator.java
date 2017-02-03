package com.afterlogic.aurora.drive.data.common.network;

import com.afterlogic.aurora.drive.core.consts.Const;

import okhttp3.HttpUrl;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiConfigurator implements DynamicDomainProvider{

    private int mCurrentApiVersion = Const.ApiVersion.API_NONE;
    private HttpUrl mDomain;

    public void setDomain(HttpUrl domain, int apiVersion) {
        mDomain = domain;
        mCurrentApiVersion = apiVersion;
    }

    @Override
    public HttpUrl getDomain() {
        return mDomain;
    }

    public void setCurrentApiVersion(int currentApiVersion) {
        mCurrentApiVersion = currentApiVersion;
    }

    public int getCurrentApiVersion() {
        return mCurrentApiVersion;
    }
}
