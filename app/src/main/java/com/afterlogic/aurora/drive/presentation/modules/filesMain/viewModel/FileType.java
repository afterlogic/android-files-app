package com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileType {
    private String mCaption;
    private String mFilesType;

    public FileType(String caption, String filesType) {
        mCaption = caption;
        mFilesType = filesType;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getFilesType() {
        return mFilesType;
    }
}
