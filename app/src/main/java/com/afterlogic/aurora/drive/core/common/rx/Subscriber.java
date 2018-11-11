package com.afterlogic.aurora.drive.core.common.rx;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.Log;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.contextWrappers.Toaster;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.streams.StreamCollectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aleksandrcikin on 25.04.17.
 *
 */

public class Subscriber {

    private static final String TAG = Subscriber.class.getSimpleName();

    private final Toaster toaster;
    private final Context context;

    private final List<Class<?>> ignoreErrors = new ArrayList<>();

    @Inject
    public Subscriber(Toaster toaster, Context context) {
        this.toaster = toaster;
        this.context = context;
    }

    public Subscriber ignoreErrors(Class<?>... types) {
        return copyAndEdit(subscriber -> Stream.of(types)
                        .collect(StreamCollectors.addTo(subscriber.ignoreErrors)));
    }

    // Simple

    public <T> AnyObserver<T> justSubscribe(){
        return subscribe(null, null, null, null);
    }

    public <T> AnyObserver<T> justSubscribe(OnSubscribeAction onSubscribe){
        return subscribe(onSubscribe, null, null, null);
    }


    // With onResult

    public <T> AnyObserver<T> subscribe(Consumer<T> onResult){
        return subscribe(null, onResult, null, null);
    }

    public <T> AnyObserver<T> subscribe(Consumer<T> onResult, ErrorHandler onError){
        return subscribe(null, onResult, null, onError);
    }

    public <T> AnyObserver<T> subscribe(Consumer<T> onResult, Runnable onComplete, ErrorHandler onError){
        return subscribe(null, onResult, onComplete, onError);
    }

    public <T> AnyObserver<T> subscribe(OnSubscribeAction onSubscribe, Consumer<T> onResult){
        return subscribe(onSubscribe, onResult, null, null);
    }

    public <T> AnyObserver<T> subscribe(OnSubscribeAction onSubscribe, Consumer<T> onResult, ErrorHandler onError){
        return subscribe(onSubscribe, onResult, null, onError);
    }


    // Only onComplete

    public <T> AnyObserver<T> subscribe(Runnable onComplete){
        return subscribe(null, null, onComplete, null);
    }

    public <T> AnyObserver<T> subscribe(Runnable onComplete, ErrorHandler onError){
        return subscribe(null, null, onComplete, onError);
    }

    public <T> AnyObserver<T> subscribe(OnSubscribeAction onSubscribe, Runnable onComplete, ErrorHandler onError){
        return subscribe(onSubscribe, null, onComplete, onError);
    }


    // Full

    public <T> AnyObserver<T> subscribe(OnSubscribeAction onSubscribe,
                                        Consumer<T> onResult,
                                        Runnable onComplete,
                                        ErrorHandler errorHandler) {

        SubscriberHandledError rootError = new SubscriberHandledError();

        Consumer<Throwable> onError = error -> handleError(error, errorHandler, rootError);

        return new AnyObserverImpl<>(onSubscribe, onResult, onComplete, onError);
    }

    public Completable defaultSchedulers(Completable completable) {
        return completable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Maybe<T> defaultSchedulers(Maybe<T> maybe) {
        return maybe.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Single<T> defaultSchedulers(Single<T> single) {
        return single.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<T> defaultSchedulers(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void handleError(Throwable e, @Nullable ErrorHandler errorHandler, @Nullable SubscriberHandledError subscribeError) {
        boolean ignore = Stream.of(ignoreErrors).anyMatch(ignoreType -> ignoreType == e.getClass());

        if (ignore) {
            Log.d(this.getClass().getSimpleName(), "Error happen: " + e.getClass().getName() + ", but it was marked as 'ignore'.");
            return;
        }

        if (errorHandler == null || !errorHandler.handleError(e)) {
            if (!(e instanceof SilentError)) {
                toaster.showShort(R.string.prompt_error_occurred);
            }
            logError(e, subscribeError);
        }
    }

    private void logError(Throwable e, @Nullable SubscriberHandledError subscribeError) {
        if (e instanceof UnloggableError) return;

        Throwable errorForLog;
        if (subscribeError != null) {
            subscribeError.initCause(e);
            errorForLog = subscribeError;
        } else {
            errorForLog = e;
        }

        MyLog.majorException(errorForLog);
    }

    private Subscriber copyAndEdit(Consumer<Subscriber> editor) {
        Subscriber subscriber = new Subscriber(toaster, context);
        subscriber.ignoreErrors.addAll(this.ignoreErrors);
        editor.consume(subscriber);
        return subscriber;
    }

    public interface ErrorHandler {
        boolean handleError(Throwable error);
    }

    private interface OnSubscribeAction {
        void onSubscribe(Disposable disposable);
    }

    private class AnyObserverImpl<T> implements AnyObserver<T> {

        private final OnSubscribeAction onSubscribe;
        private final Consumer<T> onResult;
        private final Runnable onComplete;
        private final Consumer<Throwable> onError;

        private boolean onCompleteHandled = false;

        AnyObserverImpl(OnSubscribeAction onSubscribe, Consumer<T> onResult, Runnable onComplete, Consumer<Throwable> onError) {
            this.onSubscribe = onSubscribe;
            this.onResult = onResult;
            this.onComplete = onComplete;
            this.onError = onError;
        }

        @Override
        public void onSuccess(T t) {
            if (onResult != null) {
                onResult.consume(t);
            }
            handleComplete();
        }

        @Override
        public void onNext(T t) {
            if (onResult != null) {
                onResult.consume(t);
            }
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (onSubscribe != null) {
                onSubscribe.onSubscribe(d);
            }
        }

        @Override
        public void onComplete() {
            handleComplete();
        }

        @Override
        public void onError(Throwable e) {
            if (onError != null) {
                onError.consume(e);
            }
        }

        private void handleComplete() {
            if (onCompleteHandled) return;
            onCompleteHandled = true;

            if (onComplete != null) {
                onComplete.run();
            }
        }

    }
}
