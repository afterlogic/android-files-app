package com.afterlogic.aurora.drive.data.model;

import com.afterlogic.aurora.drive.data.model.project7.AuroraFileP7;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class AuroraFilesResponse {

    @SerializedName("Items")
    private List<AuroraFileP7> mFiles;

    public List<AuroraFileP7> getFiles() {
        return mFiles;
    }
}
