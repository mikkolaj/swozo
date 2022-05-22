package com.swozo.security;

import com.swozo.model.users.User;

public interface TokenService {
    AccessToken createAccessToken(User user);

    AccessToken parseAccessToken(String token);
}
