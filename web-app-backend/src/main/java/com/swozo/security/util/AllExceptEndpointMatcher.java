package com.swozo.security.util;

import org.springframework.http.HttpMethod;

import java.util.*;
import java.util.stream.Collectors;

public class AllExceptEndpointMatcher extends EndpointMatcher {
    private final List<EndpointMatcher> extraBlackList;

    public AllExceptEndpointMatcher(Set<EndpointsConfig> endpoints) {
        super(endpoints);
        this.extraBlackList = new LinkedList<>();
    }

    public static AllExceptEndpointMatcher of(EndpointsConfig... endpoints) {
        return new AllExceptEndpointMatcher(Arrays.stream(endpoints).collect(Collectors.toUnmodifiableSet()));
    }

    public AllExceptEndpointMatcher andWithoutEndpointsMatchedBy(EndpointMatcher endpointMatcher) {
        extraBlackList.add(endpointMatcher);
        return this;
    }

    @Override
    public boolean matches(String requestUri, HttpMethod httpMethod) {
        return !super.matches(requestUri, httpMethod) &&
                extraBlackList.stream().noneMatch(matcher -> matcher.matches(requestUri, httpMethod));
    }
}
