package com.afterlogic.aurora.drive.presentation.common.modules.presenter;

import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;
import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 08.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class PermissionEventObservableSource implements ObservableSource<PermissionGrantEvent> {

    private Set<Observer<? super PermissionGrantEvent>> mObservers = new HashSet<>();

    @Override
    public void subscribe(Observer<? super PermissionGrantEvent> observer) {
        mObservers.add(observer);

        observer.onSubscribe(new Disposable() {
            @Override
            public void dispose() {
                mObservers.remove(observer);
            }

            @Override
            public boolean isDisposed() {
                return mObservers.contains(observer);
            }
        });
    }

    public void onPermissionEvent(PermissionGrantEvent event){
        Stream.of(mObservers).forEach(observer -> observer.onNext(event));
    }
}
