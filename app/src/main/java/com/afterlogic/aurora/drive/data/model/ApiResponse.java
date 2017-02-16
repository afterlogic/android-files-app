package com.afterlogic.aurora.drive.data.model;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ApiResponse<T> {

    boolean isSuccess();

    T getResult();

    String getErrorMessage();

    int getErrorCode();

    long getAccountId();
}
