package com.swozo.utils;

import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class LoggingUtils {
    private LoggingUtils() {
    }

    public static <T> BiConsumer<T, Throwable> logIfSuccess(Logger logger, String template) {
        return (msg, error) -> {
            if (error == null) {
                logger.info(template, msg);
            }
        };
    }

    public static <T> BiConsumer<T, Throwable> logIfError(Logger logger, String template) {
        return (msg, error) -> {
            if (error != null) {
                logger.error(template, error);
            }
        };
    }

    public static <T> Function<Throwable, T> logAndDefault(Logger logger, String template, T fallback) {
        return error -> {
            logger.error(template, error);
            return fallback;
        };
    }

    public static <T> BiConsumer<T, Throwable> log(Logger logger, String successLog, String failureTemplate) {
        return (msg, error) -> {
            if (error == null) {
                logger.info(successLog);
            } else {
                logger.error(failureTemplate, error);
            }
        };
    }
}
