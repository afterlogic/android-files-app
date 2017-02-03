package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseError extends RuntimeException {

    public abstract int getErrorCode();
}
