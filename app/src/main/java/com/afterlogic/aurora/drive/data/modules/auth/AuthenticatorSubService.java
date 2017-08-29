package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.model.AuroraSession;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

interface AuthenticatorSubService {

    Single<AuroraSession> login(String host, String login, String pass);

    Single<AuroraSession> byToken(String host, String token);

    Maybe<Integer> getApiVersion(String host);
}
