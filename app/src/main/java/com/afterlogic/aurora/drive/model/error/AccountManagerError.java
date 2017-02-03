package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountManagerError extends BaseError {

    public static final int CODE = -3;

    @Override
    public int getErrorCode() {
        return CODE;
    }
}
