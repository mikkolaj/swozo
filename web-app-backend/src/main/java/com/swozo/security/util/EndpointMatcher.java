package com.swozo.security.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EndpointMatcher {
    protected final Set<EndpointsConfig> endpoints;

    public static EndpointMatcher of(EndpointsConfig... endpoints) {
        return new EndpointMatcher(Arrays.stream(endpoints).collect(Collectors.toUnmodifiableSet()));
    }

    public boolean matches(String requestUri, HttpMethod httpMethod) {
        var pathMatcher = new AntPathMatcher();
        return endpoints.stream().anyMatch(endpoint ->
                endpoint.methods().contains(httpMethod) && pathMatcher.match(endpoint.pathPattern(), requestUri)
        );
    }
}
