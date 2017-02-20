package com.afterlogic.aurora.drive.data.modules.cleaner;

import io.reactivex.Completable;

/**
 * Created by sashka on 21.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface DataCleaner {
    Completable cleanAllUserData();

    Completable cleanUserAccountAndSession();
}
