package com.afterlogic.aurora.drive.data.model.project8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 31.08.17.
 */

public class UserP8 {

    @SerializedName("Login")
    private String mLogin;

    @SerializedName("IncomingLogin")
    private String mIncomingLogin;

    @SerializedName("IdUser")
    private String mPublicId;

    public String getName() {
        return mIncomingLogin != null ? mIncomingLogin : mLogin;
    }

    public String getPublicId() {
        return mPublicId;
    }

}
