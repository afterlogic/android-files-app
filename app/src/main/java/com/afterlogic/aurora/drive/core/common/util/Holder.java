package com.afterlogic.aurora.drive.core.common.util;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

/**
 * Created by sashka on 05.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class Holder<T> {
    private T mValue;

    public Holder() {
    }

    public Holder(T value) {
        mValue = value;
    }

    public T get() {
        return mValue;
    }

    public void set(T value) {
        mValue = value;
    }

    public T clear(){
        T value = mValue;
        mValue = null;
        return value;
    }

    public void ifNotNull(Consumer<T> consumer){
        if (mValue != null){
            consumer.consume(mValue);
        }
    }
}
