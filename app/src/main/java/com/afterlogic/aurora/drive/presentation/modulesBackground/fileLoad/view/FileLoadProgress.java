package com.afterlogic.aurora.drive.presentation.modulesBackground.fileLoad.view;

import android.support.annotation.IntRange;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileLoadProgress {

    private String mFileName;

    @IntRange(from = 0, to = 1000)
    private int mProgress;

    public FileLoadProgress(String fileName, int progress) {
        mFileName = fileName;
        mProgress = progress;
    }

    public String getFileName() {
        return mFileName;
    }

    public int getProgress() {
        return mProgress;
    }
}
