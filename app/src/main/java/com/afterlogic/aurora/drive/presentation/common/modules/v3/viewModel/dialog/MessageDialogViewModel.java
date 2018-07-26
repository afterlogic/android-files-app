package com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog;

import androidx.databinding.ObservableField;

/**
 * Created by aleksandrcikin on 17.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MessageDialogViewModel {

    private String title;
    private String message;
    private OnCloseListener onCloseListener;

    public static void set(ObservableField<MessageDialogViewModel> field, String title, String message) {
        field.set(new MessageDialogViewModel(title, message, vm -> {
            if (vm == field.get()) {
                field.set(null);
            }
        }));
    }

    public MessageDialogViewModel(String title, String message, OnCloseListener onCloseListener) {
        this.title = title;
        this.message = message;
        this.onCloseListener = onCloseListener;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void onClose() {
        if (onCloseListener != null) {
            onCloseListener.onClose(this);
        }
    }

    void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public interface OnCloseListener {
        void onClose(MessageDialogViewModel vm);
    }
}
