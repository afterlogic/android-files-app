package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class RenameFileParameters {

    @SerializedName(Api8.Param.TYPE)
    private String type;

    @SerializedName(Api8.Param.PATH)
    private String path;

    @SerializedName(Api8.Param.NAME)
    private String name;

    @SerializedName(Api8.Param.NEW_NAME)
    private String newName;

    @SerializedName(Api8.Param.IS_LINK)
    private boolean isLink;

    public RenameFileParameters(String type, String path, String name, String newName,
                                boolean isLink) {
        this.type = type;
        this.path = path;
        this.name = name;
        this.newName = newName;
        this.isLink = isLink;
    }

}
