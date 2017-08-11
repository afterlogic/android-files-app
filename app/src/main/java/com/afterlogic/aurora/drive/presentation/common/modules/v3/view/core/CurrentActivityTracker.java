package com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.CoreScope;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */
@CoreScope
public class CurrentActivityTracker {

    @Nullable
    private Activity currentActivity;

    @Inject
    public CurrentActivityTracker() {
    }

    @Nullable
    public Activity getCurrentActivity() {
        return currentActivity;
    }

    private void setCurrentActivity(@Nullable Activity activity) {
        if (currentActivity == activity) return;

        currentActivity = activity;
    }

    private void onActivityDestroyed(Activity activity) {
        if (currentActivity != activity) return;

        currentActivity = null;
    }

    public static class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        private final CurrentActivityTracker interactor;

        @Inject
        ActivityLifecycleCallbacks(CurrentActivityTracker interactor) {
            this.interactor = interactor;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            interactor.setCurrentActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            interactor.setCurrentActivity(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            interactor.setCurrentActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            interactor.onActivityDestroyed(activity);
        }
    }
}
