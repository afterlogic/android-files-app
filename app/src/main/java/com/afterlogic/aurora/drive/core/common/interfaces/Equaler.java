package com.afterlogic.aurora.drive.core.common.interfaces;

import androidx.annotation.NonNull;

/**
 * Created by sashka on 30.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface Equaler<T> {
    boolean equals(@NonNull T first, @NonNull T second);
}
