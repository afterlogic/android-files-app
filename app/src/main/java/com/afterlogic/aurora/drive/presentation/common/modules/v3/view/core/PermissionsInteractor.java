package com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.PresentationScope;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;
import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

@PresentationScope
public class PermissionsInteractor {

    private final CurrentActivityTracker currentActivityTracker;

    private PublishSubject<PermissionGrantEvent> grantEventPublishSubject = PublishSubject.create();

    @Inject
    public PermissionsInteractor(CurrentActivityTracker currentActivityTracker) {
        this.currentActivityTracker = currentActivityTracker;
    }

    public Single<PermissionGrantEvent> requirePermission(PermissionRequest request) {
        return grantEventPublishSubject
                .doOnSubscribe(disposable -> {
                    Activity currentActivity = currentActivityTracker.getCurrentActivity();
                    if (currentActivity == null) {
                        throw new Error("Activity not present");
                    }

                    ActivityCompat.requestPermissions(
                            currentActivity, request.getPermissions(), request.getRequestCode()
                    );
                })
                .filter(event -> event.getRequestCode() == request.getRequestCode())
                .firstOrError()
                .flatMap(event -> {
                    if (event.isAllGranted()) {
                        return Single.just(event);
                    } else {
                        return Single.error(new PermissionDeniedError(
                                event.getRequestCode(), event.getPermissions()
                        ));
                    }
                });
    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        grantEventPublishSubject.onNext(new PermissionGrantEvent(requestCode, permissions, grantResults));
    }
}
