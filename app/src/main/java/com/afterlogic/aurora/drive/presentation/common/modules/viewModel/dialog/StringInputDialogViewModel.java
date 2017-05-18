package com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

/**
 * Created by sashka on 20.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StringInputDialogViewModel extends MessageDialogViewModel {

    @NonNull
    private Consumer<String> mResultConsumer;

    private int NonEditableTextEndLenght;

    public StringInputDialogViewModel(String title, String message, DialogCancelListener listener, @NonNull Consumer<String> resultConsumer, int nonEditableTextEndLenght) {
        super(title, message, listener);
        mResultConsumer = resultConsumer;
        NonEditableTextEndLenght = nonEditableTextEndLenght;
    }

    @NonNull
    public Consumer<String> getResultConsumer() {
        return mResultConsumer;
    }

    public int getNonEditableTextEndLenght() {
        return NonEditableTextEndLenght;
    }
}
