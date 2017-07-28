package com.afterlogic.aurora.drive.core.common.rx;

import com.annimon.stream.Collector;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

/**
 * Created by sashka on 13.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class Observables {

    public static CompletableTransformer completeOnError(Class<? extends Throwable> skipClass){
        return upstream -> upstream.onErrorResumeNext(error -> {
            if (skipClass == error.getClass()){
                return Completable.complete();
            } else {
                return Completable.error(error);
            }
        });
    }

    public static <T> ObservableTransformer<T, T> emptyOnError(Class<? extends Throwable> skipClass){
        return upstream -> upstream.onErrorResumeNext(error -> {
            if (skipClass == error.getClass()){
                return Observable.empty();
            } else {
                return Observable.error(error);
            }
        });
    }

    public static  <T> Observable<T> forEach(Observable<List<T>> observable){
        return observable.flatMap(items -> Stream.of(items)
                .map(Observable::just)
                .collect(Collectors.concatObservables())
        );
    }

    public static class Collectors {
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

        public static <T> Collector<Observable<T>, List<Observable<T>>, Observable<T>> concatObservables() {

            return new Collector<Observable<T>, List<Observable<T>>, Observable<T>>() {
                @Override
                public Supplier<List<Observable<T>>> supplier() {
                    return ArrayList::new;
                }

                @Override
                public BiConsumer<List<Observable<T>>, Observable<T>> accumulator() {
                    return List::add;
                }

                @Override
                public Function<List<Observable<T>>, Observable<T>> finisher() {
                    return Observable::concat;
                }
            };
        }
    }
}
