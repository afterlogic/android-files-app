package com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.error.ActivityResultError;
import com.afterlogic.aurora.drive.model.error.ViewNotPresentError;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.presentation.common.components.view.SelectionEditText;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.CurrentActivityTracker;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 14.07.17.
 * mail: mail@sunnydaydev.me
 */

public class BaseViewInteractor {

    private final CurrentActivityTracker currentActivityTracker;

    @Inject
    public BaseViewInteractor(CurrentActivityTracker currentActivityTracker) {
        this.currentActivityTracker = currentActivityTracker;
    }

    protected Maybe<String> getInputDialog(int title) {
        return getInputDialog(title, null, null);
    }

    protected Maybe<String> getInputDialog(int title, @Nullable Consumer<SelectionEditText> initializer, @Nullable EditTextInputHandler handler) {
        List<Runnable> finalizers = new ArrayList<>();

        return Maybe.<String>create(emitter -> {

            Activity activity = currentActivityTracker.getCurrentActivity();
            if (activity == null) {
                emitter.onError(new ViewNotPresentError());
                return;
            }

            Application.ActivityLifecycleCallbacks lifecycleCallbacks = new EmptyActivityLifecycleCallbacks() {
                @Override
                public void onActivityStopped(Activity stoppedActivity) {
                    super.onActivityStopped(stoppedActivity);
                    if (stoppedActivity == activity) {
                        emitter.onComplete();
                    }
                }
            };

            Application app = activity.getApplication();
            app.registerActivityLifecycleCallbacks(lifecycleCallbacks);
            finalizers.add(() -> app.unregisterActivityLifecycleCallbacks(lifecycleCallbacks));

            @SuppressLint("InflateParams")
            View inputRootView = LayoutInflater.from(activity)
                    .inflate(R.layout.item_layout_dialog_input, null);
            SelectionEditText input = inputRootView.findViewById(R.id.input);

            if (initializer != null) {
                initializer.consume(input);
            }

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
                    String inputString = input.getText().toString();
                    try {
                        String handledInput;
                        if (handler == null) {
                            handledInput = inputString.trim();
                            if (TextUtils.isEmpty(handledInput)) {
                                throw new EditTextInputError(activity.getString(R.string.error_field_required));
                            }
                        } else {
                            handledInput = handler.onHandleInput(inputString);
                        }

                        emitter.onSuccess(handledInput);
                        input.clearFocus();

                    } catch (EditTextInputError error) {
                        input.setError(error.getMessage());
                        input.requestFocus();
                    }
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

    protected Single<ActivityResultEvent> checkSuccess(Single<ActivityResultEvent> upstream) {
        return upstream.flatMap(event -> {
            if (event.isSuccess()) {
                return Single.just(event);
            } else {
                return Single.error(new ActivityResultError(-1));
            }
        });
    }

    protected interface EditTextInputHandler {
        String onHandleInput(String input) throws EditTextInputError;
    }

    protected class EditTextInputError extends Throwable {

        public EditTextInputError(String message) {
            super(message);
        }

    }
}
