package com.swozo.utils;

import org.slf4j.Logger;

import java.util.function.BiConsumer;

public class LoggingUtils {
    private LoggingUtils() {
    }

    public static <T> BiConsumer<T, Throwable> logIfSuccess(Logger logger, String template) {
        return (msg, error) -> {
            if (msg != null) {
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

    public static <T> BiConsumer<T, Throwable> log(Logger logger, String successTemplate, String failureTemplate) {
        return (msg, error) -> {
            if (msg != null) {
                logger.info(successTemplate, msg);
            } else if (error != null) {
                logger.error(failureTemplate, error);
            }
        };
    }
}
