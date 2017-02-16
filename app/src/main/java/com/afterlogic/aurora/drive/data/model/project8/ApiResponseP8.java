package com.afterlogic.aurora.drive.data.model.project8;

import com.afterlogic.aurora.drive.data.model.ApiResponse;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiResponseP8<T> implements ApiResponse<T>{

    public static final int INCONSISTENT_RESPONSE = 1;

    public static final String TAG_RESULT = "Result";
    public static final String TAG_ERROR_CODE = "ErrorCode";
    public static final String TAG_ERROR_MESSAGE = "ErrorMessage";

    private static final int NO_ERROR = -1;

    /**
     * Module : StandardAuth
     * Method : Login
     * Result : {"AuthToken":"token_value"}
     */

    @SerializedName("AuthentificatedAccountID")
    private long mAccountId;

    @SerializedName("Module")
    private String mModule;
    @SerializedName("Method")
    private String mMethod;
    /**
     * AuthToken : token_value
     */

    @SerializedName(TAG_RESULT)
    private T mResult;

    @SerializedName(TAG_ERROR_CODE)
    private int mErrorCode = NO_ERROR;

    @SerializedName(TAG_ERROR_MESSAGE)
    private String mErrorMessage;

    public static <T> ApiResponseP8<T> error(int code, String message){
        ApiResponseP8<T> response = new ApiResponseP8<>();
        response.mErrorCode = code;
        response.mErrorMessage = message;
        return response;
    }

    public ApiResponseP8() {
    }

    public ApiResponseP8(String module, String method, T result) {
        mModule = module;
        mMethod = method;
        mResult = result;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    public String getModule() {
        return mModule;
    }

    public String getMethod() {
        return mMethod;
    }

    @Override
    public T getResult() {
        return mResult;
    }

    @Override
    public boolean isSuccess(){
        return mErrorCode == NO_ERROR;
    }

    @Override
    public long getAccountId() {
        return mAccountId;
    }

    @Override
    public String getErrorMessage() {
        return mErrorMessage;
    }
}
