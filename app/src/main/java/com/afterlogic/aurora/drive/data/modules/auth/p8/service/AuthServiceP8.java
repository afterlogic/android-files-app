package com.afterlogic.aurora.drive.data.modules.auth.p8.service;

import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;

import io.reactivex.Single;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface AuthServiceP8 {
    Single<ApiResponseP8<AuthToken>> login(String login, String password);
}
