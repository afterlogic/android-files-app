package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.model.AuthorizedAuroraSession;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

interface AuthenticatorSubService {

    Single<AuthorizedAuroraSession> login(String host, String email, String pass);

    Single<AuthorizedAuroraSession> byToken(String host, String token);

    Single<Boolean> isExternalClientLoginFormsAvailable(String host);

    Maybe<Integer> getApiVersion(String host);
}
