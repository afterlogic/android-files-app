package com.afterlogic.aurora.drive._unrefactored.data.common.repository;

import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;

import io.reactivex.Single;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface UserRepository {
    Single<AuthToken> login(String login, String password);

    Single<AuthToken> relogin();

    Single<SystemAppData> getSystemAppData();
}
