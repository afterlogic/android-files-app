package com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.ActivityResultEventSource;
import com.afterlogic.aurora.drive.core.common.rx.PermissionEventSource;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.error.ActivityResultError;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;
import com.afterlogic.aurora.drive.model.error.ViewNotPresentError;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 14.07.17.
 * mail: mail@sunnydaydev.me
 */

public class BaseViewInteractor {

    private final OptWeakRef<Fragment> weakView = OptWeakRef.empty();

    private final EventBus bus;

    @Inject
    public BaseViewInteractor(EventBus bus) {
        this.bus = bus;
    }

    public void bindView(Fragment fragment) {
        weakView.set(fragment);
    }

    public void clearView() {
        weakView.clear();
    }

    protected Completable requireWritePermission(int requestId, String[] permissions) {
        return PermissionEventSource.create(bus)
                .startWith(requestPermission(requestId, permissions).toObservable())
                .filter(event -> event.getRequestId() == requestId)
                .firstOrError()
                .flatMapCompletable(event -> {
                    if (event.isAllGranted()) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new PermissionDeniedError(requestId, permissions));
                    }
                });
    }

    protected Single<ActivityResultEvent> requireActivityResult(int requestId) {
        return ActivityResultEventSource.create(bus, requestId);
    }

    protected Maybe<String> getInputDialog(int title) {
        List<Runnable> finalizers = new ArrayList<>();

        return Maybe.<String>create(emitter -> {
            Fragment fragment = weakView.get();
            if (fragment == null) {
                emitter.onError(new ViewNotPresentError());
                return;
            }

            Activity activity = fragment.getActivity();
            if (activity == null) {
                emitter.onError(new ViewNotPresentError());
                return;
            }

            FragmentManager.FragmentLifecycleCallbacks lifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentStopped(FragmentManager fm, Fragment f) {
                    super.onFragmentStopped(fm, f);
                    emitter.onComplete();
                }
            };
            FragmentManager fm = fragment.getFragmentManager();
            fm.registerFragmentLifecycleCallbacks(lifecycleCallbacks, false);
            finalizers.add(() -> fm.unregisterFragmentLifecycleCallbacks(lifecycleCallbacks));

            @SuppressLint("InflateParams")
            View inputRootView = LayoutInflater.from(activity)
                    .inflate(R.layout.item_layout_dialog_input, null);
            EditText input = (EditText) inputRootView.findViewById(R.id.input);

            AlertDialog dialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog)
                    .setTitle(title)
                    .setView(inputRootView)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setOnDismissListener(d -> emitter.onComplete())
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                input.requestFocus();
                finalizers.add(input::clearFocus);

                InputMethodManager keyboard = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(input, 0);

                finalizers.add(() -> keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0));

                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(view -> {

                    String newName = input.getText().toString().trim();
                    if (TextUtils.isEmpty(newName)){
                        input.setError(activity.getString(R.string.error_field_required));
                        input.requestFocus();
                        return;
                    }

                    input.clearFocus();

                    emitter.onSuccess(newName);
                });
            });

            finalizers.add(() -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        })//----|
                .doFinally(() -> Stream.of(finalizers).forEach(Runnable::run));
    }

    private Completable requestPermission(int requestId, String[] permissions) {
        return Completable.fromAction(() -> {

            Fragment fragment = weakView.get();
            if (fragment == null) {
                throw new Error("View not present.");
            }

            fragment.requestPermissions(permissions, requestId);
        });
    }

    protected Single<ActivityResultEvent> checkSuccess(Single<ActivityResultEvent> upstream) {
        return upstream.flatMap(event -> {
            if (event.isSuccess()) {
                return Single.just(event);
            } else {
                return Single.error(new ActivityResultError(-1));
            }
        });
    }
}
