package com.afterlogic.aurora.drive._unrefactored.model.project8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 23.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class UploadResultP8 {

    @SerializedName("File")
    private UploadedFile mFile;

    public boolean isSuccess() {
        return mFile != null;
    }

    public UploadedFile getFile() {
        return mFile;
    }

    public class UploadedFile{
        @SerializedName("Name")
        private String mName;
        @SerializedName("TempName")
        private String mTempName;
        @SerializedName("MimeType")
        private String mMimeType;
        @SerializedName("Size")
        private long mSize;

        public String getName() {
            return mName;
        }

        public String getTempName() {
            return mTempName;
        }

        public String getMimeType() {
            return mMimeType;
        }

        public long getSize() {
            return mSize;
        }
    }
}
