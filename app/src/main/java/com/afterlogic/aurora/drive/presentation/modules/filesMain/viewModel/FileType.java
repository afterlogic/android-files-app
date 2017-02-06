package com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileType {

    private String mCaption;
    private String mFilesType;

    public FileType(String type, String caption) {
        mFilesType = type;
        mCaption = caption;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getFilesType() {
        return mFilesType;
    }
}
