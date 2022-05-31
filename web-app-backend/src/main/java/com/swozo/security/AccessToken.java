package com.swozo.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class AccessToken extends AbstractAuthenticationToken {

    public AccessToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public abstract String getCredentials();

    public abstract long getUserId();

    public abstract boolean isExpired();

    public abstract long getExpirationTime();
}
