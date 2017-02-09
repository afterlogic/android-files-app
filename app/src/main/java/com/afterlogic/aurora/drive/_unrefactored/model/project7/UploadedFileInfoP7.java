package com.afterlogic.aurora.drive._unrefactored.model.project7;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 23.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class UploadedFileInfoP7 {

    @SerializedName("Name")
    private String mName;

    @SerializedName("TempName")
    private String mTempName;

    @SerializedName("MimeType")
    private String mContentType;

    @SerializedName("Hash")
    private String mHash;

    @SerializedName("Size")
    private long mSize;

    public String getName() {
        return mName;
    }

    public String getTempName() {
        return mTempName;
    }

    public String getContentType() {
        return mContentType;
    }

    public String getHash() {
        return mHash;
    }

    public long getSize() {
        return mSize;
    }
}
