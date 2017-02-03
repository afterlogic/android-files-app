package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AuthError extends BaseError {

    public static final int CODE = 102;

    @Override
    public int getErrorCode() {
        return CODE;
    }
}
