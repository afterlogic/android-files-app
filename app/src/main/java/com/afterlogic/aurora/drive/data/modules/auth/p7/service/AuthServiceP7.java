package com.afterlogic.aurora.drive.data.modules.auth.p7.service;

import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;

import io.reactivex.Single;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface AuthServiceP7 {
    Single<ApiResponseP7<AuthToken>> login(String login, String pass);

    Single<ApiResponseP7<SystemAppData>> getSystemAppData();
}
