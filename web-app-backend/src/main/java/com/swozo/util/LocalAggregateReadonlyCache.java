package com.swozo.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Cache provider that lazily revalidates whole cache every 'revalidateAfter' period.
 * Returned values can't be edited because they are NOT deep-copied.
 * Before cache is initially populated 'cacheMissSupplier' can be called multiple times,
 * after first population slightly stale data can be returned.
 */
public class LocalAggregateReadonlyCache<T> {
    private final Duration revalidateAfter;
    private final AtomicReference<Map<String, T>> cache;
    private final AtomicReference<LocalDateTime> lastEvictionTime;
    private final AtomicBoolean isFetching;
    private final Function<T, String> keyExtractor;
    private final Runnable cacheMissObserver;

    public LocalAggregateReadonlyCache(
            Duration serviceCacheRevalidateAfter,
            Function<T, String> keyExtractor,
            Runnable cacheMissObserver
    ) {
        this.revalidateAfter = serviceCacheRevalidateAfter;
        this.keyExtractor = keyExtractor;
        this.cacheMissObserver = cacheMissObserver;
        this.cache = new AtomicReference<>(Map.of());
        this.lastEvictionTime = new AtomicReference<>(LocalDateTime.now());
        this.isFetching = new AtomicBoolean(false);
    }

    public List<T> getAll(Supplier<List<T>> cacheMissSupplier) {
        if (cacheRequiresRevalidation()) {
            populateCache(cacheMissSupplier);
        }

        return cache.get().values().stream().toList();
    }

    public Optional<T> get(String key, Supplier<List<T>> cacheMissSupplier) {
        if (cacheRequiresRevalidation()) {
            populateCache(cacheMissSupplier);
        }

        return Optional.ofNullable(cache.get().get(key));
    }

    private void populateCache(Supplier<List<T>> cacheMissSupplier) {
        if (isFetching.get() && !cache.get().isEmpty())
            return;

        cacheMissObserver.run();

        try {
            isFetching.set(true);
            cache.set(cacheMissSupplier.get().stream()
                    .collect(Collectors.toUnmodifiableMap(
                            keyExtractor,
                            Function.identity())
                    )
            );
            lastEvictionTime.set(LocalDateTime.now());
        } finally {
            isFetching.set(false);
        }
    }

    private boolean cacheRequiresRevalidation() {
        return cache.get().isEmpty() ||
                LocalDateTime.now().minus(revalidateAfter).isAfter(lastEvictionTime.get());
    }
}

