package com.afterlogic.aurora.drive.presentation.common.util;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 18.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LazyProvider<T> {

    private T mValue;
    private final Provider<T> mProvider;

    @Inject
    public LazyProvider(Provider<T> provider) {
        mProvider = provider;
    }

    public void init(){
        if (mValue == null) {
            mValue = mProvider.get();
        }
    }

    public void reset(){
        mValue = null;
    }

    public void ifPresent(Consumer<T> consumer){
        if (mValue != null){
            consumer.consume(mValue);
        }
    }
}
