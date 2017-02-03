package com.afterlogic.aurora.drive.core.common.util;

import com.afterlogic.aurora.drive.core.common.interfaces.AppErrorConsumer;
import com.afterlogic.aurora.drive.model.error.BaseError;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ErrorUtil {

    public static void ifAppError(Throwable throwable, AppErrorConsumer errorConsumer){
        if (throwable instanceof BaseError){
            errorConsumer.onError((BaseError) throwable, ((BaseError) throwable).getErrorCode());
        }
    }
}
