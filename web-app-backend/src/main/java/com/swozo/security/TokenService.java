package com.swozo.security;


import com.swozo.persistence.user.User;

import java.time.Duration;

public interface TokenService {
    AccessToken createAccessToken(User user);

    AccessToken createRefreshToken(User user);

    AccessToken parseAccessToken(String token);

    Duration getRefreshTokenExpirationTime();
}
