package com.swozo.security;


import com.swozo.persistence.user.User;

public interface TokenService {
    AccessToken createAccessToken(User user);

    AccessToken parseAccessToken(String token);
}
