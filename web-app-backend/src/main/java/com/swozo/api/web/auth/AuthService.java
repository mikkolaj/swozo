package com.swozo.api.web.auth;

import com.swozo.api.web.auth.dto.AuthDetailsDto;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.auth.request.LoginRequest;
import com.swozo.api.web.user.UserRepository;
import com.swozo.mapper.UserMapper;
import com.swozo.persistence.user.User;
import com.swozo.security.AccessToken;
import com.swozo.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.swozo.security.util.AuthUtils.GRANTED_AUTHORITY_PREFIX;
import static com.swozo.security.util.AuthUtils.getUsersAuthorities;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final Optional<RoleHierarchy> roleHierarchy;
    private final UserMapper userMapper;

    public AuthDetailsDto authenticateUser(LoginRequest loginRequest) {
        // TODO check passwd etc
        var user = userRepository.findByEmail(loginRequest.email()).orElseThrow();
        var token = tokenService.createAccessToken(user);
        var appRoles = user.getRoles().stream().map(userMapper::roleToDto).toList();

        // duplicate some data stored in token for easier access on frontend
        return new AuthDetailsDto(token.getCredentials(), token.getExpirationTime(), appRoles);
    }

    public boolean hasRole(AccessToken accessToken, RoleDto role) {
        return getUserRoles(accessToken.getAuthorities()).contains(role);
    }

    public boolean hasRole(User user, RoleDto role) {
        return getUserRoles(getUsersAuthorities(user)).contains(role);
    }

    /**
     * @param possibleRoles - access token MUST provide exactly one of specified roles
     * @throws IllegalStateException - user has none or more than one of specified roles
     */
    public RoleDto oneOf(AccessToken accessToken, RoleDto ...possibleRoles) {
        var userRoles = getUserRoles(accessToken.getAuthorities());
        var matchingRoles = Arrays.stream(possibleRoles).filter(userRoles::contains).toList();

        if (matchingRoles.size() != 1) {
            throw new IllegalStateException(String.format("Failed to get one of [%s] roles, %d is matching from [%s]",
                    Arrays.toString(possibleRoles), matchingRoles.size(), userRoles));
        }

        return matchingRoles.get(0);
    }

    private List<RoleDto> getUserRoles(Collection<? extends GrantedAuthority> authorities) {
        return withUnwrappedRoleHierarchy(authorities).stream()
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

    private Set<? extends GrantedAuthority> withUnwrappedRoleHierarchy(Collection<? extends GrantedAuthority> authorities) {
        return new HashSet<>(roleHierarchy.isPresent() ? roleHierarchy.get().getReachableGrantedAuthorities(authorities) : authorities);
    }
}
