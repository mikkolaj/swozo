package com.swozo.security.jwt;

import com.swozo.security.AccessToken;

import java.util.Date;

public class JwtAccessToken extends AccessToken {
    private final String token;
    private final long expireTime; // epoch time
    private final long uuid;

    public JwtAccessToken(String token, long uuid, long expireTime) {
        super(null);
        this.token = token;
        this.uuid = uuid;
        this.expireTime = expireTime;
    }

    @Override
    public String getPrincipal() {
        return String.valueOf(uuid);
    }

    @Override
    public boolean isAuthenticated() {
        return token != null;
    }

    @Override
    public String getName() {
        return getPrincipal();
    }

    @Override
    public String getCredentials() {
        return token;
    }

    public long getUserId() {
        return uuid;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public boolean isExpired() {
        return expireTime - new Date().toInstant().getEpochSecond() <= 0;
    }
}
