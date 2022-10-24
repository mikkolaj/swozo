package com.swozo.security.rules.jwt;

import com.swozo.security.AuthRule;
import com.swozo.security.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class JwtAuthRule implements AuthRule {
    private static final String JWT_AUTH_PREFIX = "Bearer";

    private final Logger logger = LoggerFactory.getLogger(JwtAuthRule.class);
    private final JwtTokenService tokenService;

    @Autowired
    public JwtAuthRule(JwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) throws UnauthorizedException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(JWT_AUTH_PREFIX)) {
            throw new SecurityException("Expected Authorization header with prefix: " + JWT_AUTH_PREFIX);
        }

        try {
            String token = authHeader.substring(JWT_AUTH_PREFIX.length());
            return tokenService.parseAccessToken(token);
        } catch (Exception exception) {
            logger.debug("Jwt auth failed", exception);
            throw new UnauthorizedException("Authentication failed");
        }
    }
}
