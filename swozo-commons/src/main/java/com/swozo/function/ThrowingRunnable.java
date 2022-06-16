package com.swozo.function;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
