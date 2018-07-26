package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class DeleteFilesParameters {

    @SerializedName(Api8.Param.TYPE)
    private String type;

    @SerializedName(Api8.Param.ITEMS)
    private List<DeleteFileInfo> items;

    public DeleteFilesParameters(String type, List<DeleteFileInfo> items) {
        this.type = type;
        this.items = items;
    }

}
