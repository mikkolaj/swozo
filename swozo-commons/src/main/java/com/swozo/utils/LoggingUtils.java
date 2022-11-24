package com.swozo.utils;

import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class LoggingUtils {
    private LoggingUtils() {
    }

    public static <T> BiConsumer<T, Throwable> logTemplateIfSuccess(Logger logger, String template) {
        return (msg, error) -> {
            if (error == null) {
                logger.info(template, msg);
            }
        };
    }

    public static <T> Function<Throwable, T> logAndDefault(Logger logger, String template, T fallback) {
        return error -> {
            logger.error(template, error);
            return fallback;
        };
    }

    public static <T> BiConsumer<T, Throwable> log(Logger logger, String successLog, String failureTemplate, Class<?> acceptableException) {
        return (msg, error) -> {
            logIfSuccess(logger, successLog).accept(msg, error);
            logIfError(logger, failureTemplate, acceptableException).accept(msg, error);
        };
    }

    public static <T> BiConsumer<T, Throwable> log(Logger logger, String successLog, String failureTemplate) {
        return (msg, error) -> {
            logIfSuccess(logger, successLog).accept(msg, error);
            logIfError(logger, failureTemplate).accept(msg, error);
        };
    }

    public static <T> BiConsumer<T, Throwable> logIfSuccess(Logger logger, String successLog) {
        return (msg, error) -> {
            if (error == null) {
                logger.info(successLog);
            }
        };
    }

    public static <T> BiConsumer<T, Throwable> logIfError(Logger logger, String message, Class<?> acceptableException) {
        return (msg, error) -> {
            if (error != null) {
                if (acceptableException.isInstance(error.getCause())) {
                    logger.warn(message, error);
                } else {
                    logIfError(logger, message).accept(msg, error);
                }
            }
        };
    }

    public static <T> BiConsumer<T, Throwable> logIfError(Logger logger, String message) {
        return (msg, error) -> {
            if (error != null) {
                logger.error(message, error);
            }
        };
    }
}
