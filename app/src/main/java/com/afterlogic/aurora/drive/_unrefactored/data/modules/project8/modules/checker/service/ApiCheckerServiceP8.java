package com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.checker.service;

import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ApiCheckerServiceP8 {
    Single<ApiResponseP8<String>> ping(HttpUrl url);
}
