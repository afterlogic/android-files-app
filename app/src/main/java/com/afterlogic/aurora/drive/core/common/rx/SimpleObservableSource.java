package com.afterlogic.aurora.drive.core.common.rx;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 09.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SimpleObservableSource<T> implements ObservableSource<T> {

    private final Set<Observer<? super T>> mObservers = new HashSet<>();

    @Override
    public void subscribe(Observer<? super T> observer) {
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

    public void onNext(T value){
        Stream.of(mObservers).forEach(observer -> observer.onNext(value));
    }

    public void complete(){
        List<Observer<? super T>> observers = new ArrayList<>(mObservers);
        mObservers.clear();
        Stream.of(observers).forEach(Observer::onComplete);
    }

    public void clear(){
        mObservers.clear();
    }
}
