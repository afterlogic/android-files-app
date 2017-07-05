package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import android.databinding.ObservableList;

/**
 * Created by sashka on 10.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleOnListChangedCallback<T extends ObservableList> extends ObservableList.OnListChangedCallback<T> {

    public static <T> void addTo(ObservableList<T> list, OnChangedCallback<ObservableList<T>> callback) {
        list.addOnListChangedCallback(new SimpleOnListChangedCallback<>(callback));
    }

    private OnChangedCallback<T> mCallback;

    public SimpleOnListChangedCallback(OnChangedCallback<T> callback) {
        mCallback = callback;
    }

    @Override
    public void onChanged(T t) {
        mCallback.onChange(t);
    }

    @Override
    public void onItemRangeChanged(T t, int i, int i1) {
        mCallback.onChange(t);
    }

    @Override
    public void onItemRangeInserted(T t, int i, int i1) {
        mCallback.onChange(t);
    }

    @Override
    public void onItemRangeMoved(T t, int i, int i1, int i2) {
        mCallback.onChange(t);
    }

    @Override
    public void onItemRangeRemoved(T t, int i, int i1) {
        mCallback.onChange(t);
    }
}
