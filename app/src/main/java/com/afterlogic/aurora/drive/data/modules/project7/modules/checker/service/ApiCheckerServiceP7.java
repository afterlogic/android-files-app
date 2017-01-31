package com.afterlogic.aurora.drive.data.modules.project7.modules.checker.service;

import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.model.project7.ApiResponseP7;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ApiCheckerServiceP7 {
    Single<ApiResponseP7<SystemAppData>> getSystemAppData(HttpUrl url);
}
