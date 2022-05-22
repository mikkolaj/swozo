package com.swozo.api.auth;

import com.swozo.api.auth.dto.AuthData;
import com.swozo.api.auth.dto.LoginData;
import com.swozo.model.users.UserRepository;
import com.swozo.security.TokenService;
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
        var user = userRepository.findByEmail(loginData.email()).orElseThrow();
        var token = tokenService.createAccessToken(user);
        return new AuthData(token.getCredentials(), token.getExpireTime());
    }
}
