package com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog;

import androidx.annotation.Nullable;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MessageDialogViewModel {
    private String mTitle;
    private String mMessage;
    private DialogCancelListener mListener;

    public MessageDialogViewModel(String title, String message, DialogCancelListener listener) {
        mTitle = title;
        mMessage = message;
        mListener = listener;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    @Nullable
    public DialogCancelListener getCancelListener() {
        return mListener;
    }
}
