package com.afterlogic.aurora.drive.model;

import android.support.annotation.NonNull;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class Storage {

    private String mCaption;
    private String mType;

    public Storage(@NonNull String type, @NonNull String caption) {
        mType = type;
        mCaption = caption;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getFiles() {
        return mType;
    }

}
