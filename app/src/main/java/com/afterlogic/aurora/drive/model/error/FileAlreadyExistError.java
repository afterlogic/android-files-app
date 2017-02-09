package com.afterlogic.aurora.drive.model.error;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 08.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileAlreadyExistError extends BaseError {

    private final AuroraFile mCheckedFile;

    public FileAlreadyExistError(AuroraFile checkedFile) {
        mCheckedFile = checkedFile;
    }

    @Override
    public int getErrorCode() {
        return -5;
    }

    public AuroraFile getCheckedFile() {
        return mCheckedFile;
    }
}
