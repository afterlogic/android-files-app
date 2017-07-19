package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SyncProgress {
    private String mFile;
    private String mFileName;
    private int mProgress;
    private boolean mIsDone;

    public SyncProgress(String file, String fileName, int progress, boolean isDone) {
        mFile = file;
        mFileName = fileName;
        mProgress = progress;
        mIsDone = isDone;
    }

    public String getFilePathSpec() {
        return mFile;
    }

    public String getFileName() {
        return mFileName;
    }

    public int getProgress() {
        return mProgress;
    }

    public boolean isDone() {
        return mIsDone;
    }
}
