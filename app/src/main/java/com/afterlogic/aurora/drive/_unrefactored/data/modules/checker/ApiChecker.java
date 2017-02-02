package com.afterlogic.aurora.drive._unrefactored.data.modules.checker;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ApiChecker {
    Single<Integer> getApiVersion(HttpUrl domen);
}
