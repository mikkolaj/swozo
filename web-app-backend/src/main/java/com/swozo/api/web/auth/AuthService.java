package com.swozo.api.web.auth;

import com.swozo.api.web.auth.dto.AuthDetailsDto;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.auth.request.LoginRequest;
import com.swozo.api.web.user.UserRepository;
import com.swozo.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthDetailsDto authenticateUser(LoginRequest loginRequest) {
        // TODO check passwd etc
        var user = userRepository.findByEmail(loginRequest.email()).orElseThrow();
        var token = tokenService.createAccessToken(user);
        var appRoles = user.getRoles().stream().map(RoleDto::from).toList();

        // duplicate some data stored in token for easier access on frontend
        return new AuthDetailsDto(token.getCredentials(), token.getExpirationTime(), appRoles);
    }
}
