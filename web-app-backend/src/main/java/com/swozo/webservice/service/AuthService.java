package com.swozo.webservice.service;

import com.swozo.dto.auth.AppRole;
import com.swozo.dto.auth.AuthData;
import com.swozo.dto.auth.LoginData;
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

    public AuthData authenticateUser(LoginData loginData) {
        // TODO check passwd etc
        var user = userRepository.findByEmail(loginData.email()).orElseThrow();
        var token = tokenService.createAccessToken(user);
        var appRoles = user.getRoles().stream().map(AppRole::from).toList();

        // duplicate some data stored in token for easier access on frontend
        return new AuthData(token.getCredentials(), token.getExpirationTime(), appRoles);
    }
}
