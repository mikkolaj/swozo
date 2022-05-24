package com.swozo.security.util;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EndpointMatcher {
    private final Set<String> pathPatterns;

    public EndpointMatcher(Set<String> pathPatterns) {
        this.pathPatterns = pathPatterns;
    }

    public static EndpointMatcher of(String... pathPatterns) {
        return new EndpointMatcher(Arrays.stream(pathPatterns).collect(Collectors.toUnmodifiableSet()));
    }

    public boolean matches(String requestUri, HttpMethod httpMethod) {
        // matching on http method may be useful in the future, doesn't work for now
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return pathPatterns.stream().anyMatch(pathPattern -> pathMatcher.match(pathPattern, requestUri));
    }
}
