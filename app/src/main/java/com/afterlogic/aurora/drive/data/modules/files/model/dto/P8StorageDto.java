package com.afterlogic.aurora.drive.data.modules.files.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 30.11.2017.
 * mail: mail@sunnydaydev.me
 */

public class P8StorageDto {

    @SerializedName("Type")
    private String Type;
    @SerializedName("DisplayName")
    private String DisplayName;
    @SerializedName("IsExternal")
    private boolean IsExternal;

    public String getType() {
        return Type;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public boolean getIsExternal() {
        return IsExternal;
    }

}
