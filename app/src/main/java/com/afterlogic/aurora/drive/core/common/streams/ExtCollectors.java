package com.afterlogic.aurora.drive.core.common.streams;

import com.annimon.stream.Collector;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ExtCollectors {

    public static <T>Collector<T, ArrayList<T>, ArrayList<T>> toArrayList(){
        return new Collector<T, ArrayList<T>, ArrayList<T>>() {
            @Override
            public Supplier<ArrayList<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<ArrayList<T>, T> accumulator() {
                return ArrayList::add;
            }

            @Override
            public Function<ArrayList<T>, ArrayList<T>> finisher() {
                return list -> list;
            }
        };
    }

    public static <T> Collector<T, Collection<T>, Collection<T>> to(Collection<T> target){
        return new Collector<T, Collection<T>, Collection<T>>(){

            @Override
            public Supplier<Collection<T>> supplier() {
                return () -> target;
            }

            @Override
            public BiConsumer<Collection<T>, T> accumulator() {
                return Collection::add;
            }

            @Override
            public Function<Collection<T>, Collection<T>> finisher() {
                return list -> list;
            }
        };
    }
}
