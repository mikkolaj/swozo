package com.swozo.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class AccessToken extends AbstractAuthenticationToken {
    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public AccessToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public abstract String getCredentials();

    public abstract long getUserId();

    public abstract boolean isExpired();

    public abstract long getExpireTime();
}
