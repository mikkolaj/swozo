package com.swozo.security;

import com.swozo.databasemodel.users.User;

public interface TokenService {
    AccessToken createAccessToken(User user);

    AccessToken parseAccessToken(String token);
}
