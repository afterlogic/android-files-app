package com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core;

import android.app.Activity;
import android.content.Intent;

import com.afterlogic.aurora.drive.application.ActivityTracker;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.PresentationScope;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

@PresentationScope
public class ActivityResultInteractor {

    private final ActivityTracker activityTracker;

    private PublishSubject<ActivityResultEvent> activityResultEventPublishSubject = PublishSubject.create();

    @Inject
    public ActivityResultInteractor(ActivityTracker activityTracker) {
        this.activityTracker = activityTracker;
    }

    public Single<ActivityResultEvent> waitResult(int requestId) {
        return activityResultEventPublishSubject
                .filter(event -> event.getRequestCode() == requestId)
                .firstOrError();
    }

    public Single<ActivityResultEvent> startRequest(StartActivityForResultRequest request) {
        return waitResult(request.getRequestId())
                .doOnSubscribe(disposable -> {

                    Activity currentActivity = activityTracker.getLastActiveActivity();

                    if (currentActivity == null) {
                        throw new Error("Activity not present");
                    }

                    currentActivity.startActivityForResult(request.getIntent(), request.getRequestId());

                });
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResultEventPublishSubject.onNext(new ActivityResultEvent(requestCode, resultCode, data));
    }

}
