package com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ProgressViewModel {

    private final String title;
    private final String message;
    private final ProgressBar progressBar;

    public ProgressViewModel(String title, String message, ProgressBar progressBar) {
        this.title = title;
        this.message = message;
        this.progressBar = progressBar;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public interface ProgressBar {
        boolean isIndeterminate();
        int getProgress();
        int getMax();
    }

    public static class CircleProgressBar implements ProgressBar {

        @Override
        public boolean isIndeterminate() {
            return true;
        }

        @Override
        public int getProgress() {
            return 0;
        }

        @Override
        public int getMax() {
            return 0;
        }
    }

    public static class LineProgressBar implements ProgressBar {

        private final boolean indeterminate;
        private final int progress;
        private final int max;

        LineProgressBar(boolean indeterminate, int progress, int max) {
            this.indeterminate = indeterminate;
            this.progress = progress;
            this.max = max;
        }

        @Override
        public boolean isIndeterminate() {
            return indeterminate;
        }

        @Override
        public int getProgress() {
            return progress;
        }

        @Override
        public int getMax() {
            return max;
        }
    }

    public static class Factory {

        public static ProgressViewModel indeterminateCircle(String title, String message) {
            return new ProgressViewModel(title, message, new CircleProgressBar());
        }

        public static ProgressViewModel indeterminateProgress(String title, String message) {
            LineProgressBar bar = new LineProgressBar(true, 0, 0);
            return new ProgressViewModel(title, message, bar);
        }

        public static ProgressViewModel progress(String title, String message, int progress, int max) {
            LineProgressBar bar = new LineProgressBar(false, progress, max);
            return new ProgressViewModel(title, message, bar);
        }

    }
}
