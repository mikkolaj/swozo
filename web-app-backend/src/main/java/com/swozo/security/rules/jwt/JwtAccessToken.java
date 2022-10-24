package com.swozo.security.rules.jwt;

import com.swozo.security.AccessToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

public class JwtAccessToken extends AccessToken {
    private final String token;
    private final long expirationTime; // epoch time in seconds
    private final long uuid;

    public JwtAccessToken(String token, long uuid, long expirationTime, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.uuid = uuid;
        this.expirationTime = expirationTime;
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

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return expirationTime - new Date().toInstant().getEpochSecond() <= 0;
    }
}
