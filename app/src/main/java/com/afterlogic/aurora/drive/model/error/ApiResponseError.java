package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 18.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiResponseError extends BaseError {

    private int mCode;

    public ApiResponseError(String message, int code) {
        super(message);
        mCode = code;
    }

    @Override
    public int getErrorCode() {
        return mCode;
    }
}
