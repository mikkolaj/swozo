package com.swozo.config;

import com.swozo.api.auth.dto.AppRole;
import com.swozo.security.AuthConstraint;
import com.swozo.security.jwt.JwtAuthRule;
import com.swozo.security.jwt.JwtTokenService;
import com.swozo.security.util.AllExceptEndpointMatcher;
import com.swozo.security.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AuthConstrainsConfig {
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthConstrainsConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public List<AuthConstraint> authConstraints() {
        var constraints = new LinkedList<AuthConstraint>();

        var jwtRoutes = AllExceptEndpointMatcher.of(
                "/auth/**", // auth
                "/v3/**",               // swagger
                "/swagger-ui/**"      // swagger UI
        );

        constraints.add(new AuthConstraint(new JwtAuthRule(jwtTokenService), jwtRoutes));

        return constraints;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        var roleHierarchy = new RoleHierarchyImpl();

        // TODO not sure about relation between teacher and technical teacher roles
        var admin = AuthUtils.toSpringRole(AppRole.ADMIN);

        var hierarchy = Arrays.stream(AppRole.values())
                .filter(role -> role != AppRole.ADMIN)
                .map(AuthUtils::toSpringRole)
                .map(role -> admin + " > " + role)
                .collect(Collectors.joining("\n"));

        // hierarchy += "\n ROLE_TECHNICAL_TEACHER > ROLE_TEACHER";

        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
