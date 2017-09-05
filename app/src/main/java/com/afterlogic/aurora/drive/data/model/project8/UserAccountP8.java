package com.afterlogic.aurora.drive.data.model.project8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 31.08.17.
 */

public class UserAccountP8 {

    @SerializedName("id")
    private long id;

    @SerializedName("login")
    private String login;

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }
}
