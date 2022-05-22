package com.swozo.security.filters;

import com.swozo.security.AuthConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private final List<AuthConstraint> authConstraints;

    @Autowired
    public AuthFilter(List<AuthConstraint> authConstraints) {
        this.authConstraints = authConstraints;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // will throw Security or Unauthorized exception if authentication fails
        authConstraints.stream()
                .filter(authConstraint -> authConstraint.appliesTo(request))
                .findAny()
                .map(authConstraint -> authConstraint.authenticate(request))
                .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));

        filterChain.doFilter(request, response);
    }
}
