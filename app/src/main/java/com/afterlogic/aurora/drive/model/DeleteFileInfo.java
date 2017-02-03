package com.afterlogic.aurora.drive.model;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 25.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class DeleteFileInfo {

    @SerializedName(Api8.Param.PATH)
    private String mPath;

    @SerializedName(Api8.Param.NAME)
    private String mName;

    public DeleteFileInfo(String path, String name) {
        mPath = path;
        mName = name;
    }
}
