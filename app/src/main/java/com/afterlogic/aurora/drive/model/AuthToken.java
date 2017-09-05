package com.afterlogic.aurora.drive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class AuthToken {

    @SerializedName("AuthToken")
    public String token;

    public long userId = -1;

    public AuthToken(String token) {
        this.token = token;
    }

    public AuthToken() { }
}
