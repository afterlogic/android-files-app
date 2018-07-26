package com.afterlogic.aurora.drive.data.modules.files.model.dto;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ShortFileDto {

    @SerializedName(Api8.Param.NAME)
    private String name;

    @SerializedName(Api8.Param.IS_FOLDER)
    private boolean isFolder;

    @SerializedName(Api8.Param.FROM_PATH)
    private String fromPath;

    @SerializedName(Api8.Param.FROM_TYPE)
    private String fromType;

    public ShortFileDto(String name, boolean isFolder, String path, String type) {
        this.name = name;
        this.isFolder = isFolder;
        this.fromPath = path;
        this.fromType = type;
    }

    public String getFromType() {
        return fromType;
    }
}
