package com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ProgressViewModel {

    private final String title;
    private final String message;
    private final boolean isIndeterminate;
    private final int progress;
    private final int max;

    public static ProgressViewModel indeterminate(String title, String message) {
        return new ProgressViewModel(title, message, true, 0, 0);
    }

    public ProgressViewModel(String title, String message, boolean isIndeterminate, int progress, int max) {
        this.title = title;
        this.message = message;
        this.isIndeterminate = isIndeterminate;
        this.progress = progress;
        this.max = max;
    }

    public String getTitle() {
        return title;
    }

    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }

    public String getMessage() {
        return message;
    }
}
