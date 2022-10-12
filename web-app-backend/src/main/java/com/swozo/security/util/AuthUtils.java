package com.swozo.security.util;

import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.persistence.Role;
import com.swozo.persistence.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class AuthUtils {
    public static String GRANTED_AUTHORITY_PREFIX = "ROLE_";

    private AuthUtils() {
    }

    public static Collection<? extends GrantedAuthority> getUsersAuthorities(User user) {
        return getUsersAuthorities(user.getRoles().stream().map(Role::getName).toList());
    }

    public static Collection<? extends GrantedAuthority> getUsersAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(GRANTED_AUTHORITY_PREFIX + role))
                .toList();
    }

    public static String toSpringRole(RoleDto role) {
        return GRANTED_AUTHORITY_PREFIX + role.toString();
    }
}
