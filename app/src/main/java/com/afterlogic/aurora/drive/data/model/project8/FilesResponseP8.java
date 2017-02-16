package com.afterlogic.aurora.drive.data.model.project8;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesResponseP8 {

    @SerializedName("Items")
    private List<AuroraFileP8> mFiles;

    public List<AuroraFileP8> getFiles() {
        return mFiles;
    }
}
