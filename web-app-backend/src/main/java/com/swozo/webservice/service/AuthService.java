package com.swozo.webservice.service;

import com.swozo.dto.auth.AuthDetailsDto;
import com.swozo.dto.auth.LoginRequest;
import com.swozo.dto.auth.RoleDto;
import com.swozo.security.TokenService;
import com.swozo.webservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public AuthDetailsDto authenticateUser(LoginRequest loginRequest) {
        // TODO check passwd etc
        var user = userRepository.findByEmail(loginRequest.email()).orElseThrow();
        var token = tokenService.createAccessToken(user);
        var appRoles = user.getRoles().stream().map(RoleDto::from).toList();

        // duplicate some data stored in token for easier access on frontend
        return new AuthDetailsDto(token.getCredentials(), token.getExpirationTime(), appRoles);
    }
}
