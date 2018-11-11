package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import androidx.databinding.BaseObservable;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by aleksandrcikin on 12.05.17.
 * mail: mail@sunnydaydev.me
 */

public class ObservableNotificationBag {

    private Set<BaseObservable> items = new HashSet<>();

    public static ObservableNotificationBag create(BaseObservable... fields) {
        ObservableNotificationBag bag = new ObservableNotificationBag();
        bag.add(fields);
        return bag;
    }

    public void add(BaseObservable... fields) {
        Stream.of(fields).forEach(items::add);
    }

    public void notifyChange() {
        Stream.of(items).forEach(BaseObservable::notifyChange);
    }
}
