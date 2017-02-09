package com.afterlogic.aurora.drive._unrefactored.model.project7;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 23.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class UploadResultP7 {

    @SerializedName("File")
    private UploadedFileInfoP7 mFile;

    public UploadedFileInfoP7 getFile() {
        return mFile;
    }
}
