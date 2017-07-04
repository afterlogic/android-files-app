package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import android.databinding.Observable;

/**
 * Created by sashka on 10.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleOnPropertyChangedCallback extends Observable.OnPropertyChangedCallback {

    public static void addTo(Observable field, Runnable action) {
        field.addOnPropertyChangedCallback(new SimpleOnPropertyChangedCallback(action));
    }

    private Runnable mAction;

    public SimpleOnPropertyChangedCallback(Runnable action) {
        mAction = action;
    }

    @Override
    public void onPropertyChanged(Observable observable, int i) {
        mAction.run();
    }
}
