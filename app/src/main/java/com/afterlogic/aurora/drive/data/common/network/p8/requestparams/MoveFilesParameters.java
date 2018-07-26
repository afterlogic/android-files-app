package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ShortFileDto;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class MoveFilesParameters {

    @SerializedName(Api8.Param.TO_PATH)
    private String toPath;

    @SerializedName(Api8.Param.TO_TYPE)
    private String toType;

    @SerializedName(Api8.Param.FROM_TYPE)
    private String fromType;

    @SerializedName(Api8.Param.FILES)
    private List<ShortFileDto> files;

    public MoveFilesParameters(String fromType,
                               String toPath,
                               String toType,
                               List<ShortFileDto> files) {
        this.toPath = toPath;
        this.toType = toType;
        this.files = files;
        this.fromType = fromType;
    }

}
