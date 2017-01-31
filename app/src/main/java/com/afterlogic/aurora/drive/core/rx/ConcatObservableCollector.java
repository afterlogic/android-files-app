package com.afterlogic.aurora.drive.core.rx;

import com.annimon.stream.Collector;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ConcatObservableCollector<T> implements Collector<Observable<T>, List<Observable<T>>, Observable<T>> {

    public static <T> ConcatObservableCollector<T> create(){
        return new ConcatObservableCollector<>();
    }

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
}
