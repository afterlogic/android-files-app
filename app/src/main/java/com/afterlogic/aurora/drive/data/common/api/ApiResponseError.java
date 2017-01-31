package com.afterlogic.aurora.drive.data.common.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ApiResponseError implements ApiError {

    public static final int INVALID_TOKEN = 101;
    public static final int AUTH_FAILED = 102;
    public static final int INVALID_DATA = 103;
    public static final int DATABASE_ERROR = 104;
    public static final int UNKNOWN = 999;
    public static final int FILE_NOT_EXIST = UNKNOWN;

    public static final int RESULT_FALSE = 10001;

    public ApiResponseError(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    public ApiResponseError() {
    }

    @SerializedName("ErrorCode")
    private int mCode = ERROR_CODE_NOT_EXIST;

    @SerializedName("ErrorMessage")
    private String mMessage;

    @Override
    public String getMessage() {
        return mMessage;
    }

    @Override
    public int getCode() {
        return mCode;
    }
}
