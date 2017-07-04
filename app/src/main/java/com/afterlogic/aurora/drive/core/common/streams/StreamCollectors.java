package com.afterlogic.aurora.drive.core.common.streams;

import com.afterlogic.aurora.drive.core.common.util.ListUtil;
import com.annimon.stream.Collector;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrcikin on 16.05.17.
 * mail: mail@sunnydaydev.me
 */

public class StreamCollectors {

    public static  <T> Collector<T, List<T>, List<T>> toList(final Consumer<List<T>> consumer) {
        return new Collector<T, List<T>, List<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public Function<List<T>, List<T>> finisher() {
                return result -> {
                    consumer.accept(result);
                    return result;
                };
            }
        };
    }

    public static  <T> Collector<T, List<T>, List<T>> setListByClearAdd(final List<T> list) {
        return new Collector<T, List<T>, List<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public Function<List<T>, List<T>> finisher() {
                return result -> {
                    list.clear();
                    list.addAll(result);
                    return list;
                };
            }
        };
    }

    public static  <T> Collector<T, List<T>, List<T>> setListItemByItem(final List<T> list) {
        return new Collector<T, List<T>, List<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public Function<List<T>, List<T>> finisher() {
                return result -> {
                    ListUtil.setListItemByItem(result, list);
                    return list;
                };
            }
        };
    }


    public static  <T> Collector<T, List<T>, List<T>> addTo(List<T> list) {
        return new Collector<T, List<T>, List<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public Function<List<T>, List<T>> finisher() {
                return result -> {
                    list.addAll(result);
                    return list;
                };
            }
        };
    }

    public static Collector<Character, StringBuilder, String> charsToString() {
        return new Collector<Character, StringBuilder, String>() {
            @Override
            public Supplier<StringBuilder> supplier() {
                return StringBuilder::new;
            }

            @Override
            public BiConsumer<StringBuilder, Character> accumulator() {
                return StringBuilder::append;
            }

            @Override
            public Function<StringBuilder, String> finisher() {
                return StringBuilder::toString;
            }
        };
    }

}
