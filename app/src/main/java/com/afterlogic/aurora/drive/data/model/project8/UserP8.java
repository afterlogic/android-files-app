package com.afterlogic.aurora.drive.data.model.project8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 31.08.17.
 */

public class UserP8 {

    @SerializedName("@Object")
    private String objectType;//@Object;

    @SerializedName("Name")
    private String Name;

    @SerializedName("PublicId")
    private String PublicId;

    @SerializedName("Role")
    private int Role;

    @SerializedName("WriteSeparateLog")
    private boolean WriteSeparateLog;

    public String getObjectType() {
        return objectType;
    }

    public String getName() {
        return Name;
    }

    public String getPublicId() {
        return PublicId;
    }

    public int getRole() {
        return Role;
    }

    public boolean getWriteSeparateLog() {
        return WriteSeparateLog;
    }
}
