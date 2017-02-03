package com.afterlogic.aurora.drive.core.common.interfaces;

import com.afterlogic.aurora.drive.model.error.BaseError;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface AppErrorConsumer {
    void onError(BaseError error, int code);
}
