package com.afterlogic.aurora.drive.data.modules.apiChecker.p8.service;

import com.afterlogic.aurora.drive.data.model.project8.ApiResponseP8;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ApiCheckerServiceP8 {
    Single<ApiResponseP8<String>> ping(HttpUrl url);
}
