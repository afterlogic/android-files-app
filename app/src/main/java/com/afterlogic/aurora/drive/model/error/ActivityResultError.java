package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 08.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ActivityResultError extends BaseError{
    public static final int CODE = -6;

    private final int mRequestId;

    public ActivityResultError(int requestId) {
        super("Activity result failed. id: " + requestId);
        mRequestId = requestId;
    }

    @Override
    public int getErrorCode() {
        return CODE;
    }

    public int getRequestId() {
        return mRequestId;
    }
}
