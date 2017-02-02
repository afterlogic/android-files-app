package com.afterlogic.aurora.drive._unrefactored.data.common.error;

/**
 * Created by sashka on 18.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ObservableApiError extends Throwable {

    private int mCode;

    public ObservableApiError(String message, int code) {
        super(message);
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
