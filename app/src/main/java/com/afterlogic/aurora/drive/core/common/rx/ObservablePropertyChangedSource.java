package com.afterlogic.aurora.drive.core.common.rx;

import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by aleksandrcikin on 11.05.17.
 * mail: mail@sunnydaydev.me
 */

public class ObservablePropertyChangedSource {

    public static <T extends android.databinding.Observable> Observable<T> create(T field) {
        List<Runnable> finalizers = new ArrayList<>();
        return Observable.<T>create(emitter -> {
            SimpleOnPropertyChangedCallback callback = new SimpleOnPropertyChangedCallback(() -> emitter.onNext(field));
            field.addOnPropertyChangedCallback(callback);

            finalizers.add(() -> field.removeOnPropertyChangedCallback(callback));
        })//----|
                .doFinally(() -> Stream.of(finalizers).forEach(Runnable::run));
    }

    public static <T extends android.databinding.Observable> Observable<T> createWithValue(T field) {
        return create(field).startWith(field);
    }
}
