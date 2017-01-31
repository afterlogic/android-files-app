package com.afterlogic.aurora.drive.core.rx;

import com.afterlogic.aurora.drive.core.MyLog;
import com.annimon.stream.Collector;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Supplier;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;

/**
 * Created by sashka on 13.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class Observables {

    public static class Collectors{

        public static <T> ConcatObservableCollector<T> concatObservable(){
            return new ConcatObservableCollector<T>();
        }

        public static Collector<Completable, List<Completable>, Completable> concatCompletable(){
            return new Collector<Completable, List<Completable>, Completable>() {
                @Override
                public Supplier<List<Completable>> supplier() {
                    return ArrayList::new;
                }

                @Override
                public BiConsumer<List<Completable>, Completable> accumulator() {
                    return List::add;
                }

                @Override
                public com.annimon.stream.function.Function<List<Completable>, Completable> finisher() {
                    return Completable::concat;
                }
            };
        }
    }

    public static class Logs{

        public static <T> ObservableTransformer<T, T> logLifeObservable(String tag){
            return new ObservableTransformer<T, T>() {
                @Override
                public Observable<T> apply(Observable<T> observable){
                    return observable
                            .doOnSubscribe(disposable -> MyLog.d(tag, "onSubscribe()"))
                            .doOnNext(result -> MyLog.d(tag, "onNext(" + result + ")"))
                            .doOnComplete(() -> MyLog.d(tag, "doOnComplete()"))
                            .doOnError(error -> MyLog.d(tag, "doOnError(" + error.getClass().getName() + ")"))
                            .doOnTerminate(() -> MyLog.d(tag, "doOnTerminate()"))
                            .doOnDispose(() -> MyLog.d(tag, "doOnDispose()"));
                }
            };
        }

        public static <T> MaybeTransformer<T, T> logLifeMaybe(String tag){
            return new MaybeTransformer<T, T>() {
                @Override
                public Maybe<T> apply(Maybe<T> observable){
                    return observable
                            .doOnSubscribe(disposable -> MyLog.d(tag, "onSubscribe()"))
                            .doOnSuccess(result -> MyLog.d(tag, "doOnSuccess(" + result + ")"))
                            .doOnComplete(() -> MyLog.d(tag, "doOnComplete()"))
                            .doOnError(error -> MyLog.d(tag, "doOnError(" + error.getClass().getName() + ")"))
                            .doFinally(() -> MyLog.d(tag, "doOnTerminate()"))
                            .doOnDispose(() -> MyLog.d(tag, "doOnDispose()"));
                }
            };
        }

        public static <T> SingleTransformer<T, T> logLifeSingle(String tag){
            return new SingleTransformer<T, T>() {
                @Override
                public Single<T> apply(Single<T> observable){
                    return observable
                            .doOnSubscribe(disposable -> MyLog.d(tag, "onSubscribe()"))
                            .doOnSuccess(result -> MyLog.d(tag, "doOnSuccess(" + result + ")"))
                            .doOnError(error -> MyLog.d(tag, "doOnError(" + error.getClass().getName() + ")"))
                            .doFinally(() -> MyLog.d(tag, "doFinally()"))
                            .doOnDispose(() -> MyLog.d(tag, "doOnDispose()"));
                }
            };
        }

        public static CompletableTransformer logLifeCompletable(String tag){
            return new CompletableTransformer() {
                @Override
                public Completable apply(Completable observable){
                    return observable
                            .doOnSubscribe(disposable -> MyLog.d(tag, ":onSubscribe()"))
                            .doOnComplete(() -> MyLog.d(tag, "doOnComplete()"))
                            .doOnError(error -> MyLog.d(tag, "doOnError(" + error.getClass().getName() + ")"))
                            .doFinally(() -> MyLog.d(tag, "doFinally()"))
                            .doOnDispose(() -> MyLog.d(tag, "doOnDispose()"));
                }
            };
        }
    }
}
