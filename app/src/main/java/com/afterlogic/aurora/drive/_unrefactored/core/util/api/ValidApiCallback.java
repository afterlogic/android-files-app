package com.afterlogic.aurora.drive._unrefactored.core.util.api;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive.model.error.ApiError;

/**
 * Created by sashka on 24.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ValidApiCallback<T> implements ApiCallback<T> {

    private ApiCallback<T> mCallback;
    private Validator mValidator;

    public ValidApiCallback(@NonNull ApiCallback<T> callback, @NonNull Validator validator) {
        mCallback = callback;
        mValidator = validator;
    }

    @Override
    public void onSucces(T result) {
        if (mValidator.isValide()){
            mCallback.onSucces(result);
        }
    }

    @Override
    public void onError(ApiError error) {
        if (mValidator.isValide()){
            mCallback.onError(error);
        }
    }

    public interface Validator {
        boolean isValide();
    }
}
