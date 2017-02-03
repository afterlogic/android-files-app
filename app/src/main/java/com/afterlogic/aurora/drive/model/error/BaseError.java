package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseError extends RuntimeException {

    public BaseError() {
    }

    public BaseError(String message) {
        super(message);
    }

    public BaseError(Throwable cause) {
        super(cause);
    }

    public abstract int getErrorCode();
}
