package com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public enum ViewModelState {
    CONTENT, EMPTY, LOADING, ERROR;

    public boolean isContent() {
        return this == CONTENT;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isLoading() {
        return this == LOADING;
    }

    public boolean isError() {
        return this == ERROR;
    }
}
