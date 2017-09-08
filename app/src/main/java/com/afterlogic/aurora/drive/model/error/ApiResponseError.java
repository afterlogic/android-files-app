package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 18.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiResponseError extends BaseError implements ApiError{

    public static final int INVALID_TOKEN = 101;
    public static final int AUTH_FAILED = 102;
    public static final int INVALID_DATA = 103;
    public static final int DATABASE_ERROR = 104;
    public static final int UNKNOWN = 999;
    public static final int FILE_NOT_EXIST = UNKNOWN;
    public static final int MODULE_NOT_EXIST = 113;
    public static final int METHOD_NOT_EXIST = 114;

    public static final int RESULT_FALSE = 10001;

    private int mCode;

    public ApiResponseError(int code, String message) {
        super(message);
        mCode = code;
    }

    @Override
    public int getErrorCode() {
        return mCode;
    }
}
