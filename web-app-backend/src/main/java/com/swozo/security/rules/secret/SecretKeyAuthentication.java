package com.swozo.security.rules.secret;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SecretKeyAuthentication<T> extends AbstractAuthenticationToken {
    protected final byte[] key;
    protected final T principal;

    public SecretKeyAuthentication(byte[] key, T principal) {
        super(null);
        this.key = key;
        this.principal = principal;
    }

    @Override
    public T getPrincipal() {
        return principal;
    }

    @Override
    public byte[] getCredentials() {
        return key;
    }

    @Override
    public boolean isAuthenticated() {
        return key != null && key.length > 0;
    }
}
