package com.swozo.api.common.files.util;

@FunctionalInterface
public interface FilePathGenerator {
    String generate(String filename);
}
