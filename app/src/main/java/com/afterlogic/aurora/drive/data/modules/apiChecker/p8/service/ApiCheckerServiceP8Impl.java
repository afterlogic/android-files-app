package com.afterlogic.aurora.drive.data.modules.apiChecker.p8.service;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.data.common.network.p8.CloudServiceP8;
import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiCheckerServiceP8Impl extends CloudServiceP8 implements ApiCheckerServiceP8 {

    private final Api8 mApi;

    @SuppressWarnings("WeakerAccess")
    @Inject public ApiCheckerServiceP8Impl(@P8 @NonNull Gson gson, Api8 api) {
        super(Api8.Module.CORE, gson);
        mApi = api;
    }

    @Override
    public Single<ApiResponseP8<String>> ping(HttpUrl url){
        Map<String, Object> fields = getDefaultFields(Api8.Method.PING, Collections.emptyMap());
        return mApi.ping(url.toString() + Api8.API, fields);
    }
}
