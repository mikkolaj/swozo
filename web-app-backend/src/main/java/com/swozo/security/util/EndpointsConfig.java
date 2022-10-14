package com.swozo.security.util;


import org.springframework.http.HttpMethod;

import java.util.List;

public record EndpointsConfig(String pathPattern, List<HttpMethod> methods) {
    public static EndpointsConfig of(String pathPattern) {
        return new EndpointsConfig(pathPattern, anyHttpMethod());
    }

    public static EndpointsConfig of(String pathPattern, HttpMethod... methods) {
        return new EndpointsConfig(pathPattern, List.of(methods));
    }

    public static List<HttpMethod> anyHttpMethod() {
        return List.of(HttpMethod.values());
    }
}
