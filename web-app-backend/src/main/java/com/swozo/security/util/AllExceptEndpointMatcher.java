package com.swozo.security.util;

import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AllExceptEndpointMatcher extends EndpointMatcher {

    public AllExceptEndpointMatcher(Set<String> pathPatterns) {
        super(pathPatterns);
    }

    public static AllExceptEndpointMatcher of(String... pathPatterns) {
        return new AllExceptEndpointMatcher(Arrays.stream(pathPatterns).collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public boolean matches(String requestUri, HttpMethod httpMethod) {
        return !super.matches(requestUri, httpMethod);
    }
}
