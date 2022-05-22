package com.swozo.security;

import com.swozo.security.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AuthRule {
    Authentication authenticate(HttpServletRequest request) throws UnauthorizedException, SecurityException;
}
