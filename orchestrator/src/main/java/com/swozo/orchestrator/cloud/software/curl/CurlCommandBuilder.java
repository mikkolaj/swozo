package com.swozo.orchestrator.cloud.software.curl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CurlCommandBuilder {
    private String httpMethod;
    private String url;
    private String outputLocation;
    private final List<String> headers = new ArrayList<>();
    private final List<String> formEntries = new ArrayList<>();

    public CurlCommandBuilder addHttpMethod(String method) {
        this.httpMethod = method;
        return this;
    }

    public CurlCommandBuilder addHttpHeader(String key, String value) {
        this.headers.add(String.format("%s:%s", key, value));
        return this;
    }

    public CurlCommandBuilder addFileSource(String path) {
        return addFormParam("data", String.format("@%s", path));
    }

    public CurlCommandBuilder addFormParam(String key, String value) {
        this.formEntries.add(String.format("%s=%s", key, value));
        return this;
    }

    public CurlCommandBuilder addUrl(String url) {
        this.url = url;
        return this;
    }

    public CurlCommandBuilder addOutputLocation(String outputLocation) {
        this.outputLocation = outputLocation;
        return this;
    }

    public String build() {
        if (url == null) {
            throw new IllegalStateException("URL must be present");
        }
        var headerParams = headers.stream().map(header -> String.format("-H '%s'", header));
        var formParams = formEntries.stream().map(entry -> String.format("-F '%s'", entry));

        return Stream.of(
                Stream.of("curl"),
                Optional.ofNullable(httpMethod).map(method -> String.format("-X %s", method)).stream(),
                headerParams,
                formParams,
                Stream.of(url),
                Optional.ofNullable(outputLocation).map(output -> String.format("--output '%s'", output)).stream()
        ).flatMap(Function.identity()).collect(Collectors.joining(" "));
    }
}
