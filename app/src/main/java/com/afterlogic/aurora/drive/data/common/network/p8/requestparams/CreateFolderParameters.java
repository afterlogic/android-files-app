package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class CreateFolderParameters {

    @SerializedName(Api8.Param.TYPE)
    private String type;

    @SerializedName(Api8.Param.PATH)
    private String path;

    @SerializedName(Api8.Param.FOLDER_NAME)
    private String name;

    public CreateFolderParameters(String type, String path, String name) {
        this.type = type;
        this.path = path;
        this.name = name;
    }

}
