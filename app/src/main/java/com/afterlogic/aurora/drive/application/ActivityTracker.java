package com.afterlogic.aurora.drive.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.AppScope;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */
@AppScope
public class ActivityTracker {

    @Nullable
    private Activity lastActiveActivity;

    private AtomicInteger startedCount = new AtomicInteger(0);

    @Inject
    public ActivityTracker() { }

    @Nullable
    public Activity getLastActiveActivity() {
        return lastActiveActivity;
    }

    public boolean hasStartedActivity() {
        return startedCount.get() > 0;
    }

    void register(Application application) {
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            setLastActiveActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setLastActiveActivity(activity);
            startedCount.incrementAndGet();
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setLastActiveActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            startedCount.decrementAndGet();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ActivityTracker.this.onActivityDestroyed(activity);
        }
    };

    private void setLastActiveActivity(@Nullable Activity activity) {
        if (lastActiveActivity == activity) return;

        lastActiveActivity = activity;
    }

    private void onActivityDestroyed(Activity activity) {
        if (lastActiveActivity != activity) return;

        lastActiveActivity = null;
    }
}
