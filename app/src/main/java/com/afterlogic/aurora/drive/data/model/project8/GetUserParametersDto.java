package com.afterlogic.aurora.drive.data.model.project8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sunny on 31.08.17.
 */

public class GetUserParametersDto {

    @SerializedName("AuthToken")
    private String authToken;

    public GetUserParametersDto(String authToken) {
        this.authToken = authToken;
    }

}
