package com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.PresentationScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

@PresentationScope
public class ActivityResolver {

    private OptWeakRef<Activity> activity = OptWeakRef.empty();

    private PublishSubject<ActivityResultEvent> activityResultEventPublishSubject = PublishSubject.create();
    private PublishSubject<PermissionGrantEvent> permissionGrantEventPublishSubject = PublishSubject.create();

    @Inject
    public ActivityResolver() {
    }

    public void setResolver(Activity activity) {
        this.activity.set(activity);
    }

    public void removeResolver() {
        activity.clear();
    }

    @Nullable
    public Activity getActivity() {
        return activity.get();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResultEventPublishSubject.onNext(new ActivityResultEvent(requestCode, resultCode, data));
    }

    public void onPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionGrantEventPublishSubject.onNext(new PermissionGrantEvent(requestCode, permissions, grantResults));
    }

    public Observable<ActivityResultEvent> listenActivityResult(int requestCode) {
        return activityResultEventPublishSubject
                .filter(event -> event.getRequestCode() == requestCode);
    }

    public Observable<PermissionGrantEvent> listenPermissionGrantResult(int requestCode) {
        return permissionGrantEventPublishSubject
                .filter(event -> event.getRequestCode() == requestCode);
    }
}
