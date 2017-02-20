package com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog;

import android.support.annotation.Nullable;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ProgressDialogViewModel {

    private String mTitle;
    private String mMessage;
    private int mMax;
    private int mProgress;
    @Nullable
    private DialogCancelListener mCancelListener;

    public ProgressDialogViewModel(String title, String message, int max, int progress,
                                   @Nullable DialogCancelListener cancelListener) {
        mTitle = title;
        mMessage = message;
        mMax = max;
        mProgress = progress;
        mCancelListener = cancelListener;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getMax() {
        return mMax;
    }

    public int getProgress() {
        return mProgress;
    }

    @Nullable
    public DialogCancelListener getCancelListener() {
        return mCancelListener;
    }

}
