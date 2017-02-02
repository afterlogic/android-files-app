package com.afterlogic.aurora.drive.data.modules.prefs;

/**
 * Created by sashka on 03.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface Pref<T> {
    T get();
    void set(T value);
}
