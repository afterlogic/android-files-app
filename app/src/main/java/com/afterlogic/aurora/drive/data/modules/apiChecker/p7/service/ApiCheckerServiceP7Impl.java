package com.afterlogic.aurora.drive.data.modules.apiChecker.p7.service;

import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiCheckerServiceP7Impl implements ApiCheckerServiceP7 {

    private final Api7 mApi;

    @Inject ApiCheckerServiceP7Impl(Api7 api7) {
        mApi = api7;
    }

    @Override
    public Single<ApiResponseP7<SystemAppData>> getSystemAppData(HttpUrl url) {
        return Single.defer(() -> {
            HashMap<String, Object> fields = new HashMap<>();
            fields.put(Api7.Fields.ACTION, Api7.Actions.SYSTEM_GET_APP_DATA);
            return mApi.getSystemAppData(url.toString() + Api7.AJAX, fields);
        });
    }
}
