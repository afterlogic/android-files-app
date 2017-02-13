package com.afterlogic.aurora.drive.model;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FilesSelection {

    private int mCount = 0;
    private boolean mHasFolde = false;

    public FilesSelection(int count, boolean hasFolde) {
        mCount = count;
        mHasFolde = hasFolde;
    }

    public int getCount() {
        return mCount;
    }

    public boolean hasFolder() {
        return mHasFolde;
    }
}
