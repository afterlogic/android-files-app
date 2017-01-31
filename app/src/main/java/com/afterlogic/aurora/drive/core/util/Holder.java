package com.afterlogic.aurora.drive.core.util;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class Holder<T> {
    private T mValue;

    public Holder() {
    }

    public Holder(T value) {
        mValue = value;
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }
}
