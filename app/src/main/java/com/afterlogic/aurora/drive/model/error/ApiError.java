package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public interface ApiError {
    int CONNECTION_ERROR = 1;
    int UNKNOWN_HOST = 2;
    int SESSION_NOT_EXIST = 3;
    int SESSION_NOT_COMPLETE = 4;
    int TASK_CANCELLED = 4;

    int ERROR_CODE_NOT_EXIST = Integer.MIN_VALUE;
    int NOT_FOUND = 404;

    String getMessage();
    int getErrorCode();
}