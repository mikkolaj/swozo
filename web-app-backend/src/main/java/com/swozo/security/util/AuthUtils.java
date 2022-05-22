package com.swozo.security.util;

import com.swozo.model.users.Role;
import com.swozo.model.users.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class AuthUtils {
    public static String GRANTED_AUTHORITY_PREFIX = "ROLE_";

    public static Collection<? extends GrantedAuthority> getUsersAuthorities(User user) {
        return getUsersAuthorities(user.getRoles().stream().map(Role::getName).toList());
    }

    public static Collection<? extends GrantedAuthority> getUsersAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(GRANTED_AUTHORITY_PREFIX + role))
                .toList();
    }
}
