package com.afterlogic.aurora.drive.core.common.rx;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 01.08.2018.
 * mail: mail@sunnydaydev.me
 */

@SuppressWarnings("WeakerAccess")
public interface AnyObserver<T> extends
        CompletableObserver, MaybeObserver<T>, SingleObserver<T>, Observer<T> {
}
