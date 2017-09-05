package com.afterlogic.aurora.drive.data.modules.files.model.dto;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileDto {

    @SerializedName(Api8.Param.NAME)
    private String name;

    @SerializedName(Api8.Param.IS_FOLDER)
    private boolean isFolder;

    public ReplaceFileDto(String name, boolean isFolder) {
        this.name = name;
        this.isFolder = isFolder;
    }
}
