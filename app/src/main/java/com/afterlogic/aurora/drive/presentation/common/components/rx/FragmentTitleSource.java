package com.afterlogic.aurora.drive.presentation.common.components.rx;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 15.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FragmentTitleSource implements ObservableSource<String> {

    private String mTitle;
    private final Set<Observer<? super String>> mObservers = new HashSet<>();

    public FragmentTitleSource() {
    }

    public FragmentTitleSource(String title) {
        mTitle = title;
    }

    public void setTitle(@NonNull String title) {
        mTitle = title;
        Stream.of(mObservers).forEach(observer -> observer.onNext(title));
    }

    @Override
    public void subscribe(Observer<? super String> observer) {
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
        if (mTitle != null) {
            observer.onNext(mTitle);
        }
    }
}
