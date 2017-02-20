package com.afterlogic.aurora.drive.presentation.common.modules.model.presenter;

import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
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

public class ActivityResultObservableSource implements ObservableSource<ActivityResultEvent> {

    private Set<Observer<? super ActivityResultEvent>> mObservers = new HashSet<>();

    @Override
    public void subscribe(Observer<? super ActivityResultEvent> observer) {
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

    public void onActivityResult(ActivityResultEvent event){
        Stream.of(mObservers).forEach(observer -> observer.onNext(event));
    }
}
