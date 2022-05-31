package com.swozo.security;

import com.swozo.security.exceptions.UnauthorizedException;
import com.swozo.security.util.EndpointMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class AuthConstraint {
    private final AuthRule authRule;
    private final EndpointMatcher endpointMatcher;

    public AuthConstraint(AuthRule authRule, EndpointMatcher endpointMatcher) {
        this.authRule = authRule;
        this.endpointMatcher = endpointMatcher;
    }

    public boolean appliesTo(HttpServletRequest request) {
        return endpointMatcher.matches(request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
    }

    public Authentication authenticate(HttpServletRequest request) throws UnauthorizedException, SecurityException {
        return authRule.authenticate(request);
    }
}
