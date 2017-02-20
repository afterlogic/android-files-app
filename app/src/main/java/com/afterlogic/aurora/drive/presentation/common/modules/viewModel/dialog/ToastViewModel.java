package com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ToastViewModel {
    private String mMessage;
    private long mLenght;

    public ToastViewModel(String message, long lenght) {
        mMessage = message;
        mLenght = lenght;
    }

    public String getMessage() {
        return mMessage;
    }

    public long getLenght() {
        return mLenght;
    }

    public interface OnShowListener{
        void onShowed();
    }
}
