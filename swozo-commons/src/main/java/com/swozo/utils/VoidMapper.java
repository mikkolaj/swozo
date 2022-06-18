package com.swozo.utils;

import com.swozo.function.ThrowingRunnable;

import java.util.concurrent.Callable;

public class VoidMapper {
    private VoidMapper() {
    }

    public static Callable<Void> toCallableVoid(ThrowingRunnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

    public static Void toVoid(Runnable runnable) {
        runnable.run();
        return null;
    }
}
