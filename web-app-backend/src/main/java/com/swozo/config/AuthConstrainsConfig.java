package com.swozo.config;

import com.swozo.security.AuthConstraint;
import com.swozo.security.jwt.JwtAuthRule;
import com.swozo.security.jwt.JwtTokenService;
import com.swozo.security.util.AllExceptEndpointMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class AuthConstrainsConfig {
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthConstrainsConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public List<AuthConstraint> configureAuthConstraints() {
        List<AuthConstraint> constraints = new LinkedList<>();

        var jwtRoutes = AllExceptEndpointMatcher.of(
                "/auth/**", // auth
                "/v3/**",               // swagger
                "/swagger-ui/**"        // swagger UI
        );

        constraints.add(new AuthConstraint(new JwtAuthRule(jwtTokenService), jwtRoutes));

        return constraints;
    }
}
