package com.afterlogic.aurora.drive.model;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileAction{

    private final int mId;
    private final int mText;
    private final int mIcon;
    private final boolean mCheckable;

    public FileAction(int id, int text, int icon) {
        mId = id;
        mText = text;
        mIcon = icon;
        mCheckable = false;
    }

    public FileAction(int id, int text, int icon, boolean checkable) {
        mId = id;
        mText = text;
        mIcon = icon;
        mCheckable = checkable;
    }

    public int getId() {
        return mId;
    }

    public int getText() {
        return mText;
    }

    public int getIcon() {
        return mIcon;
    }

    public boolean isCheckable() {
        return mCheckable;
    }

    public interface OnActionListener{
        void onFileAction(FileAction action);
    }
}
