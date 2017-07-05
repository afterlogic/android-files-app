package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

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
import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.error.ViewNotPresentError;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

@SubModuleScope
public class ReplaceFileTypeViewInteractor {

    private final OptWeakRef<Fragment> fragment = OptWeakRef.empty();

    @Inject
    ReplaceFileTypeViewInteractor() {
    }

    public void bind(Fragment fragment) {
        this.fragment.set(fragment);
    }

    Maybe<String> getFolderName() {

        return Maybe.create(emitter -> {

            String result = getStringFromDialog()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .blockingGet();

            if (result != null) {
                emitter.onSuccess(result);
            } else {
                emitter.onComplete();
            }
        });
    }

    private Maybe<String> getStringFromDialog() {

        List<Runnable> finalizers = new ArrayList<>();

        return Maybe.<String>create(emitter -> {
            Fragment fragment = this.fragment.get();
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
                    .setTitle(R.string.prompt_create_folder)
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
}
