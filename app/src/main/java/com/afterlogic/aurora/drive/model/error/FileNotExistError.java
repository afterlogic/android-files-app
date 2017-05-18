package com.afterlogic.aurora.drive.model.error;

/**
 * Created by sashka on 08.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileNotExistError extends BaseError {

    @Override
    public int getErrorCode() {
        return -6;
    }
}
