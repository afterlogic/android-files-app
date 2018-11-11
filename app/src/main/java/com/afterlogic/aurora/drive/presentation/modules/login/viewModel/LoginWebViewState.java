package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

/**
 * Created by sunny on 05.09.17.
 * mail: mail@sunnydaydev.me
 */

public enum LoginWebViewState {

    INITIALIZATION, NORMAL, PROGRESS, ERROR, NOT_AVAILABLE;

    public boolean isNormal() {
        return this == NORMAL;
    }

    public boolean isProgress() {
        return this == PROGRESS;
    }

    public boolean isError() {
        return this == ERROR;
    }

    public boolean isInitialization() {
        return this == INITIALIZATION;
    }

    public boolean isNotAvailable() {
        return this == NOT_AVAILABLE;
    }

    public boolean isWebViewVisible() {
        return (isNormal() || isProgress()) && !isNotAvailable();
    }

    public boolean isProgressVisible() {
        return (isProgress() || isInitialization()) && !isNotAvailable();
    }

}
