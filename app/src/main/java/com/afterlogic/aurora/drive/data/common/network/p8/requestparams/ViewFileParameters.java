package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class ViewFileParameters {

    @SerializedName(Api8.Param.TYPE)
    private String type;

    @SerializedName(Api8.Param.PATH)
    private String path;

    @SerializedName(Api8.Param.NAME)
    private String name;

    @SerializedName(Api8.Param.SHARED_HASH)
    private String publicHash;

    public ViewFileParameters(String type, String path, String name, String publicHash) {
        this.type = type;
        this.path = path;
        this.name = name;
        this.publicHash = publicHash;
    }
}
