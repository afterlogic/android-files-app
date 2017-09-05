package com.afterlogic.aurora.drive.core.common.util;

import com.afterlogic.aurora.drive.core.common.interfaces.Creator;

/**
 * Created by aleksandrcikin on 23.08.17.
 * mail: mail@sunnydaydev.me
 */

public class Lazy<T> {

    private T value;
    private boolean created = false;

    private final Creator<T> creator;

    public Lazy(Creator<T> creator) {
        this.creator = creator;
    }

    public synchronized T get() {
        if (created) {
            return value;
        } else {
            value = creator.create();
            created = true;
            return value;
        }
    }
}
