package com.swozo.orchestrator.utils;

import java.util.*;

public class CollectionUtils {
    private CollectionUtils() {
    }

    @SafeVarargs
    public static <T> List<T> combineLists(List<T>... lists) {
        var combined = new ArrayList<T>();
        for (var list : lists) {
            combined.addAll(list);
        }
        return combined;
    }

    @SafeVarargs
    public static <T> Set<T> combineSets(Set<T>... lists) {
        var combined = new HashSet<T>();
        for (var list : lists) {
            combined.addAll(list);
        }
        return combined;
    }

    @SafeVarargs
    public static <T> Set<T> addElements(Set<T> baseSet, T... elements) {
        var combined = new HashSet<>(baseSet);
        combined.addAll(Arrays.asList(elements));
        return combined;
    }
}
