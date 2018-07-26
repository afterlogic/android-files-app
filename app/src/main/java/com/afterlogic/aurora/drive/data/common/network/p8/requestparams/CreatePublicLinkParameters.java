package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class CreatePublicLinkParameters {

    @SerializedName(Api8.Param.TYPE)
    private String type;

    @SerializedName(Api8.Param.PATH)
    private String path;

    @SerializedName(Api8.Param.NAME)
    private String name;

    @SerializedName(Api8.Param.SIZE)
    private long size;

    @SerializedName(Api8.Param.IS_FOLDER)
    private boolean isFolder;

    public CreatePublicLinkParameters(
            String type, String path, String name, long size, boolean isFolder) {

        this.type = type;
        this.path = path;
        this.name = name;
        this.size = size;
        this.isFolder = isFolder;

    }

}
