package com.afterlogic.aurora.drive.core.common.util;

import com.afterlogic.aurora.drive.core.common.interfaces.AppErrorConsumer;
import com.afterlogic.aurora.drive.model.error.BaseError;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ErrorUtil {

    public static final int UNKNOWN_ERROR = Integer.MIN_VALUE + 100;

    public static void ifAppError(Throwable throwable, AppErrorConsumer errorConsumer){
        if (throwable instanceof BaseError){
            errorConsumer.onError((BaseError) throwable, getErrorCode(throwable));
        }
    }

    public static int getErrorCode(Throwable error){
        if (error instanceof BaseError){
            return ((BaseError) error).getErrorCode();
        } else {
            return UNKNOWN_ERROR;
        }
    }
}
