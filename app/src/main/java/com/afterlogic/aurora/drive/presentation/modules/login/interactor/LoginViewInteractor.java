package com.afterlogic.aurora.drive.presentation.modules.login.interactor;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.ActivityTracker;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by sunny on 01.09.17.
 */

class LoginViewInteractor {

    private final ActivityTracker activityTracker;

    @Inject
    LoginViewInteractor(ActivityTracker activityTracker) {
        this.activityTracker = activityTracker;
    }

    Single<Boolean> confirmLoginChanging(String previousLogin, String newLogin) {
        return Single.<Boolean>create(emitter -> {

            Activity activity = activityTracker.getLastActiveActivity();

            if (activity == null) {
                emitter.onError(new IllegalStateException("Doesn't have active activity."));
                return;
            }

            new AlertDialog.Builder(activity, R.style.AppTheme_Dialog)
                    .setMessage(activity.getString(R.string.prompt_relogin_dialog_change_account, previousLogin, newLogin))
                    .setPositiveButton(R.string.dialog_ok,
                            (di, which) -> emitter.onSuccess(true))
                    .setNegativeButton(R.string.dialog_cancel,
                            (di, which) -> emitter.onSuccess(false))
                    .setCancelable(false)
                    .setOnCancelListener(di -> emitter.onError(new Error("Cancelled.")))
                    .show();

        })//--->
                .subscribeOn(AndroidSchedulers.mainThread());
    }

}
