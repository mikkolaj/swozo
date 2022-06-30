package com.swozo.security;


import com.swozo.databasemodel.User;

public interface TokenService {
    AccessToken createAccessToken(User user);

    AccessToken parseAccessToken(String token);
}
