package com.afterlogic.aurora.drive.data.common.network.p8.requestparams;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class UploadFileParameters {

    @SerializedName(Api8.Param.TYPE)
    private String type;

    @SerializedName(Api8.Param.PATH)
    private String path;

    @SerializedName(Api8.Param.NAME)
    private String name;

    public UploadFileParameters(String type, String path, String name) {
        this.type = type;
        this.path = path;
        this.name = name;
    }

}
