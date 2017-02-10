package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.SystemAppData;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface AuthRepository {
    Completable login(String login, String password);

    Completable relogin();

    Single<SystemAppData> getSystemAppData();

    Maybe<AuroraSession> getCurrentSession();

    Completable logoutAndClearData();
}
