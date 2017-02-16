package com.afterlogic.aurora.drive.data.model.project7;

import com.afterlogic.aurora.drive.model.error.ApiError;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive.data.model.ApiResponse;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ApiResponseP7<T> implements ApiResponse<T>{

    public static final String TAG_ERROR_MESSAGE = "ErrorMessage";

    public static final String TAG_ERROR_CODE = "ErrorCode";

    public static final String TAG_RESULT = "Result";

    @SerializedName("AccountID")
    private long mAccountId;

    @SerializedName(TAG_RESULT)
    private T mResult;

    @SerializedName(TAG_ERROR_CODE)
    private int mErrorCode = -1;

    @SerializedName(TAG_ERROR_MESSAGE)
    private String mErrorMessage;


    public ApiResponseP7() {
    }

    public ApiResponseP7(ApiError error) {
        parseApiError(error);
    }

    public ApiResponseP7(long accountId, T result, ApiError error) {
        mAccountId = accountId;
        mResult = result;
        parseApiError(error);
    }

    private void parseApiError(ApiError error){
        if (error != null) {
            mErrorCode = error.getErrorCode();
            mErrorMessage = error.getMessage();
        } else {
            mErrorCode = -1;
            mErrorMessage = null;
        }
    }

    @Override
    public long getAccountId() {
        return mAccountId;
    }

    public T getData() {
        return mResult;
    }

    public ApiError getError() {
        if (mErrorCode == -1){
            return null;
        } else {
            return new ApiResponseError(mErrorCode, mErrorMessage);
        }
    }

    @Override
    public boolean isSuccess() {
        return getError() == null;
    }

    @Override
    public T getResult() {
        return mResult;
    }

    @Override
    public String getErrorMessage() {
        return mErrorMessage;
    }

    @Override
    public int getErrorCode() {
        return getError() == null ? 0 : getError().getErrorCode();
    }
}
