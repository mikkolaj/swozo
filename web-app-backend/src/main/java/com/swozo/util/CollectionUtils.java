package com.swozo.util;

import java.util.Collection;
import java.util.function.BiConsumer;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static <S, T> void iterateSimultaneously(Collection<S> col1, Collection<T> col2, BiConsumer<S, T> consumer) {
        var it1 = col1.iterator();
        var it2 = col2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            consumer.accept(it1.next(), it2.next());
        }
    }
}
