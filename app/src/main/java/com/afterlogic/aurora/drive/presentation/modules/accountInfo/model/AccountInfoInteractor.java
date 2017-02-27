package com.afterlogic.aurora.drive.presentation.modules.accountInfo.model;

import io.reactivex.Single;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface AccountInfoInteractor {
    Single<String> getLogin();
    Single<String> getHost();
}
