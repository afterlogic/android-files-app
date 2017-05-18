package com.afterlogic.aurora.drive.presentation.common.modules.model.interactor;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;

/**
 * Created by sashka on 29.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SuppressWarnings("WeakerAccess")
public class BaseInteractor implements Interactor {

    private ObservableScheduler mScheduler;

    private boolean mIsActive;

    public BaseInteractor(ObservableScheduler scheduler) {
        mScheduler = scheduler;
    }

    public <T> Observable<T> composeDefault(Observable<T> observable){
        return observable
                .doOnError(this::handleError)
                .compose(mScheduler::defaultSchedulers);

    }

    public <T> Maybe<T> composeDefault(Maybe<T> observable){
        return observable
                .doOnError(this::handleError)
                .compose(mScheduler::defaultSchedulers);

    }

    public <T> Single<T> composeDefault(Single<T> observable){
        return observable
                .doOnError(this::handleError)
                .compose(mScheduler::defaultSchedulers);

    }

    public Completable composeDefault(Completable observable){
        return observable
                .doOnError(this::handleError)
                .compose(mScheduler::defaultSchedulers);

    }

    public <T> Observable<T> composeImmediate(Observable<T> observable){
        return observable
                .doOnError(this::handleError)
                .compose(mScheduler::immediate);

    }

    public <T> Maybe<T> composeImmediate(Maybe<T> observable){
        return observable
                .doOnError(this::handleError)
                .compose(mScheduler::immediate);

    }

    public <T> Single<T> composeImmediate(Single<T> observable){
        return observable
                .doOnError(this::handleError)
                .compose(mScheduler::immediate);

    }

    private void handleError(Throwable error) {
        if (error instanceof CompositeException){
            CompositeException compositeException = (CompositeException) error;
            List<Throwable> exceptions = compositeException.getExceptions();
            MyLog.e(this, String.format(Locale.getDefault(), "Obtained %d composite errors:", exceptions.size()));
            Stream.of(exceptions).forEach(this::handleError);
        } else {
            MyLog.majorException(this, error);
        }
    }

    @Override
    public void onStart() {
        mIsActive = true;
    }

    @Override
    public void onStop() {
        mIsActive = false;
    }

    public boolean isActive() {
        return mIsActive;
    }
}
