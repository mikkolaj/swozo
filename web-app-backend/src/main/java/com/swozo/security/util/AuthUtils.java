package com.swozo.security.util;

import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.persistence.Role;
import com.swozo.persistence.User;
import com.swozo.security.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AuthUtils {
    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
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

    public static List<RoleDto> getUserRoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().substring(GRANTED_AUTHORITY_PREFIX.length()))
                .map(role -> {
                    try {
                        return RoleDto.valueOf(role);
                    } catch (IllegalArgumentException e) {
                        logger.error("Failed to convert granted authority to role", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static boolean hasRole(AccessToken accessToken, RoleDto role) {
        return getUserRoles(accessToken.getAuthorities()).contains(role);
    }

    public static boolean hasRole(User user, RoleDto role) {
        return user.getRoles().stream().anyMatch(userRole -> Objects.equals(RoleDto.from(userRole), role));
    }

    /**
     * @param options - access token MUST provide exactly one of specified roles
     * @throws IllegalStateException - user has none or more than one of specified roles
     */
    public static RoleDto getOneOf(AccessToken accessToken, RoleDto ...options) {
        var userRoles = getUserRoles(accessToken.getAuthorities());
        var matchingRoles = Arrays.stream(options).filter(userRoles::contains).toList();

        if (matchingRoles.size() != 1) {
            throw new IllegalStateException(String.format("Failed to get one of [%s] roles, %d is matching from [%s]",
                    Arrays.toString(options), matchingRoles.size(), userRoles));
        }

        return matchingRoles.get(0);
    }
}
