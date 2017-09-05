package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

/**
 * Created by sunny on 05.09.17.
 * mail: mail@sunnydaydev.me
 */

public enum LoginWebViewModelState {

    NORMAL, PROGRESS, ERROR;

    public boolean isNormal() {
        return this == NORMAL;
    }

    public boolean isProgress() {
        return this == PROGRESS;
    }

    public boolean isError() {
        return this == ERROR;
    }

}
