package com.swozo.orchestrator.utils;

import java.util.ArrayList;
import java.util.List;

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
}
