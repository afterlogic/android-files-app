package com.afterlogic.aurora.drive.presentation.common.binding;

import android.databinding.Observable;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleListener extends Observable.OnPropertyChangedCallback{

    private final Runnable mPropertyConsumer;

    public SimpleListener(Runnable propertyConsumer) {
        mPropertyConsumer = propertyConsumer;
    }

    @Override
    public void onPropertyChanged(Observable observable, int i) {
        mPropertyConsumer.run();
    }
}
