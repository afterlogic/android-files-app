package com.afterlogic.aurora.drive.model.project8.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuthRequestP8 {

    @SerializedName("Login")
    private String mLogin;

    @SerializedName("Password")
    private String mPassword;

    @SerializedName("SignMe")
    private boolean mSignMe;

    public AuthRequestP8(String login, String password, boolean signMe) {
        mLogin = login;
        mPassword = password;
        mSignMe = signMe;
    }
}
