package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class GetFilesParameters {

    @SerializedName(Api8.Param.TYPE)
    private String type;

    @SerializedName(Api8.Param.PATH)
    private String path;

    @SerializedName(Api8.Param.PATTERN)
    private String pattern;

    public GetFilesParameters(String type, String path, String pattern) {
        this.type = type;
        this.path = path;
        this.pattern = pattern;
    }

}
