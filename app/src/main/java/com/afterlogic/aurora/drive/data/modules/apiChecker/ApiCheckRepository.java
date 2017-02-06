package com.afterlogic.aurora.drive.data.modules.apiChecker;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ApiCheckRepository {
    Single<Boolean> checkDomain(HttpUrl domain);
}
