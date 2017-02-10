package com.afterlogic.aurora.drive.core.common.streams;

import com.annimon.stream.Collector;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.ArrayList;

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
}
