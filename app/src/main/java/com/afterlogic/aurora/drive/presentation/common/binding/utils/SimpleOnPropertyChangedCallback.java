package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import android.databinding.Observable;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.annimon.stream.Stream;

/**
 * Created by sashka on 10.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleOnPropertyChangedCallback extends Observable.OnPropertyChangedCallback {

    public static void addTo(Observable field, Runnable action) {
        field.addOnPropertyChangedCallback(new SimpleOnPropertyChangedCallback(action));
    }

    public static void addTo(Runnable action, Observable... fields) {
        Stream.of(fields).forEach(field -> addTo(field, action));
    }

    public static <T extends Observable> void addTo(T field, Consumer<T> action) {
        field.addOnPropertyChangedCallback(new SimpleOnPropertyChangedCallback(() -> action.consume(field)));
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
