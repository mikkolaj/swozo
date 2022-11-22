package com.swozo.api.web.auth;

import com.swozo.api.web.auth.dto.AuthDetailsDto;
import com.swozo.api.web.auth.dto.RefreshTokenDto;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.auth.request.LoginRequest;
import com.swozo.api.web.auth.request.ResetPasswordRequest;
import com.swozo.api.web.exceptions.types.common.ValidationErrorType;
import com.swozo.api.web.exceptions.types.common.ValidationNames;
import com.swozo.api.web.exceptions.types.user.InvalidCredentialsException;
import com.swozo.api.web.exceptions.types.user.UserNotFoundException;
import com.swozo.api.web.user.UserRepository;
import com.swozo.email.EmailService;
import com.swozo.mapper.UserMapper;
import com.swozo.persistence.user.RefreshTokenEntity;
import com.swozo.persistence.user.User;
import com.swozo.security.AccessToken;
import com.swozo.security.PasswordHandler;
import com.swozo.security.TokenService;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.swozo.security.util.AuthUtils.GRANTED_AUTHORITY_PREFIX;
import static com.swozo.security.util.AuthUtils.getUsersAuthorities;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int INITIAL_PASSWORD_LENGTH = 14;
    private static final int CHANGE_PASSWORD_TOKEN_LENGTH = 32;
    // used to limit number of refresh tokens created by user,
    // we don't want sb to fill entire database by flooding us with login requests
    private static final Duration RECENTLY_PERSISTED_TOKEN_DURATION = Duration.ofMinutes(1);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PasswordHandler passwordHandler;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Optional<RoleHierarchy> roleHierarchy;
    private final UserMapper userMapper;

    public AuthDetailsDto authenticateUser(LoginRequest loginRequest) {
        // we return same errors no matter whether user with that email doesn't exist or password is invalid,
        // even if we returned "invalid email or password" users could still be enumerated using reset password functionality
        // this shouldn't be a problem since emails are auto-generated by the organization and usually will be easy to guess anyway
        User user;
        try {
            user = userRepository.findByEmail(loginRequest.email()).orElseThrow();
            if (!checkPassword(user, loginRequest.password())) {
                throw new RuntimeException();
            }
        } catch (RuntimeException ex) {
            logger.info("Login failed for {}", loginRequest.email());
            throw new InvalidCredentialsException();
        }

        var accessToken = tokenService.createAccessToken(user);
        var refreshToken = createRefreshToken(user);

        return authDetailsFrom(accessToken, refreshToken, user);
    }

    @Transactional
    public AuthDetailsDto refreshAccessToken(RefreshTokenDto refreshToken) {
        try {
            var token = tokenService.parseAccessToken(refreshToken.refreshToken());
            var persistedToken = refreshTokenRepository.findById(refreshToken.tokenId()).orElseThrow();

            if (token.isExpired()) {
                // sanity check, token service should throw it by itself in this case
                throw new UnauthorizedException("Token expired");
            }
            if (!persistedToken.getToken().equals(token.getCredentials())) {
                throw new UnauthorizedException("Token doesn't match persisted one");
            }

            var user = userRepository.findById(token.getUserId()).orElseThrow();
            var refreshedAccessToken = tokenService.createAccessToken(user);
            return authDetailsFrom(refreshedAccessToken, refreshToken, user);
        } catch (Exception exception) {
            logger.error("Failed to refresh token: " + refreshToken, exception);
            throw new InvalidCredentialsException();
        }
    }

    @Transactional
    public void logout(RefreshTokenDto refreshTokenDto, Long userId) {
        refreshTokenRepository.findById(refreshTokenDto.tokenId()).ifPresent(refreshTokenEntity -> {
            if (!refreshTokenEntity.getUserId().equals(userId) ||
                !refreshTokenEntity.getToken().equals(refreshTokenDto.refreshToken())
            ) {
                throw new InvalidCredentialsException();
            }

            refreshTokenRepository.delete(refreshTokenEntity);
        });
    }

    public void cleanupExpiredRefreshTokens() {
        // TODO cron it
        refreshTokenRepository.deleteAllByIssuedAtBefore(
                LocalDateTime.now().minus(tokenService.getRefreshTokenExpirationTime())
        );
    }

    public void removeAllRefreshTokensForUser(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    public void sendResetPasswordEmail(String email) {
        // TODO: use IP rate limiting
        var user = findByEmail(email);
        emailService.sendResetPasswordEmail(user);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        var user = findByEmail(request.email());
        validateChangePasswordRequest(user, request.token(), request.password());

        user.setPassword(hashPassword(request.password()));
        user.setChangePasswordToken(provideChangePasswordToken());
        userRepository.save(user);

        removeAllRefreshTokensForUser(user.getId());
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(User user, String plaintextPassword) {
        return passwordEncoder.matches(plaintextPassword, user.getPassword());
    }

    public String provideInitialPassword() {
        return passwordHandler.generatePassword(INITIAL_PASSWORD_LENGTH);
    }

    public String provideChangePasswordToken() {
        return passwordHandler.generateStrongRandomToken(CHANGE_PASSWORD_TOKEN_LENGTH);
    }

    public boolean hasRole(AccessToken accessToken, RoleDto role) {
        return getUserRoles(accessToken.getAuthorities()).contains(role);
    }

    public boolean hasRole(User user, RoleDto role) {
        return getUserRoles(getUsersAuthorities(user)).contains(role);
    }

    public boolean isAdmin(Long userId) {
        // TODO ensure proper caching of admins (+ invalidate on user data update)
        return userRepository.findById(userId).map(user -> hasRole(user, RoleDto.ADMIN)).orElse(false);
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

    private AuthDetailsDto authDetailsFrom(AccessToken accessToken, RefreshTokenDto refreshToken, User user) {
        return new AuthDetailsDto(
                accessToken.getCredentials(),
                accessToken.getExpirationTime(),
                refreshToken,
                user.getRoles().stream().map(userMapper::roleToDto).toList()
        );
    }

    private RefreshTokenDto createRefreshToken(User user) {
        var recentlyPersistedToken = refreshTokenRepository.findByUserIdAndIssuedAtAfter(
                user.getId(), LocalDateTime.now().minus(RECENTLY_PERSISTED_TOKEN_DURATION)
        );

        if (recentlyPersistedToken.isPresent()) {
            try {
                var refreshToken = tokenService.parseAccessToken(recentlyPersistedToken.get().getToken());
                return new RefreshTokenDto(
                        recentlyPersistedToken.get().getId(),
                        refreshToken.getExpirationTime(),
                        refreshToken.getCredentials()
                );
            } catch (Exception ex) {
                refreshTokenRepository.delete(recentlyPersistedToken.get());
            }
        }

        var refreshToken = tokenService.createRefreshToken(user);

        var tokenEntity = new RefreshTokenEntity();
        tokenEntity.setToken(refreshToken.getCredentials());
        tokenEntity.setUserId(user.getId());

        refreshTokenRepository.save(tokenEntity);
        return new RefreshTokenDto(tokenEntity.getId(), refreshToken.getExpirationTime(), refreshToken.getCredentials());
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

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> UserNotFoundException.withEmail(email));
    }

    private void validateChangePasswordRequest(User user, String token, String password) {
        var validator = passwordHandler.validatePassword(password);
        validator.putIfFails(
                Optional.of(!user.getChangePasswordToken().equals(token))
                        .filter(Boolean::booleanValue)
                        .map(failed -> ValidationErrorType.INVALID_PASSWORD_TOKEN.forField(ValidationNames.Fields.TOKEN))
        );

        validator.build().throwIfAnyPresent("Invalid reset password request");
    }
}
