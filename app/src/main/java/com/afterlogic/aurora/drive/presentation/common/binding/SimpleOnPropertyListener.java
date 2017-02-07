package com.afterlogic.aurora.drive.presentation.common.binding;

import android.databinding.Observable;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleOnPropertyListener<T> extends Observable.OnPropertyChangedCallback{

    private final Consumer<T> mPropertyConsumer;

    public SimpleOnPropertyListener(Consumer<T> propertyConsumer) {
        mPropertyConsumer = propertyConsumer;
    }

    @Override
    public void onPropertyChanged(Observable observable, int i) {
        mPropertyConsumer.consume(((T) observable));
    }
}
