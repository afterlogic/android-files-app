package com.afterlogic.aurora.drive.core.common.util;

import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by aleksandrcikin on 06.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ListUtil {

    public static <T> void setListItemByItem(List<T> source, List<T> target) {
        synchronized (target) {
            int maxSize = Math.max(target.size(), source.size());
            Stream.range(0, maxSize)
                    .forEach(index -> {
                        if (index < target.size() && index < source.size()) {
                            target.set(index, source.get(index));
                        } else if (index < source.size()) {
                            target.add(source.get(index));
                        } else {
                            target.remove(source.size());
                        }
                    });
        }
    }
}
