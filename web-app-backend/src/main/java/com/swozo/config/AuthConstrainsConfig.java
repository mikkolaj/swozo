package com.swozo.config;

import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.security.AuthConstraint;
import com.swozo.security.keys.KeyProvider;
import com.swozo.security.rules.jwt.JwtAuthRule;
import com.swozo.security.rules.jwt.JwtTokenService;
import com.swozo.security.rules.secret.services.ServiceSecretKeyRule;
import com.swozo.security.util.AllExceptEndpointMatcher;
import com.swozo.security.util.AuthUtils;
import com.swozo.security.util.EndpointMatcher;
import com.swozo.security.util.EndpointsConfig;
import com.swozo.utils.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class AuthConstrainsConfig {
    private final JwtTokenService jwtTokenService;
    private final KeyProvider keyProvider;

    @Bean
    public List<AuthConstraint> authConstraints() {
        var orchestratorMatcher = EndpointMatcher.of(
                EndpointsConfig.of("/orchestrator-test", HttpMethod.GET)
        );

        var jwtMatcher = AllExceptEndpointMatcher.of(
                EndpointsConfig.of("/auth/**"),             // auth
                EndpointsConfig.of("/v3/**"),               // swagger
                EndpointsConfig.of("/swagger-ui/**")        // swagger UI
        ).andWithoutEndpointsMatchedBy(orchestratorMatcher);

        return List.of(
                new AuthConstraint(new JwtAuthRule(jwtTokenService), jwtMatcher),
                new AuthConstraint(new ServiceSecretKeyRule(keyProvider, ServiceType.ORCHESTRATOR), orchestratorMatcher)
        );
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        var roleHierarchy = new RoleHierarchyImpl();

        // TODO not sure about relation between teacher and technical teacher roles
        var admin = AuthUtils.toSpringRole(RoleDto.ADMIN);

        var hierarchy = Arrays.stream(RoleDto.values())
                .filter(role -> role != RoleDto.ADMIN)
                .map(AuthUtils::toSpringRole)
                .map(role -> admin + " > " + role)
                .collect(Collectors.joining("\n"));

        // hierarchy += "\n ROLE_TECHNICAL_TEACHER > ROLE_TEACHER";

        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
