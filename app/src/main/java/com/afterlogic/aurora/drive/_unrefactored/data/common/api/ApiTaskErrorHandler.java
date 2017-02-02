package com.afterlogic.aurora.drive._unrefactored.data.common.api;

/**
 * Created by sashka on 04.04.16.
 * mail: sunnyday.development@gmail.com
 */
public interface ApiTaskErrorHandler {
    void onTaskError(ApiTask.TaskException ex);
}
