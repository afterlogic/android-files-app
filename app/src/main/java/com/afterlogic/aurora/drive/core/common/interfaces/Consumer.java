package com.afterlogic.aurora.drive.core.common.interfaces;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface Consumer<T> {
    void consume(T value);
}
