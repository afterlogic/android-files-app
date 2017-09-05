package com.afterlogic.aurora.drive.data.model.project8;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 02.09.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginParametersDto {

    @SerializedName(Api8.Param.LOGIN)
    private String login;

    @SerializedName(Api8.Param.PASSWORD)
    private String password;

    @SerializedName(Api8.Param.SIGN_ME)
    private boolean singMe = false;

    public LoginParametersDto(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
