package com.swozo.security.rules.secret;

import com.swozo.security.AuthRule;
import com.swozo.security.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public abstract class SecretKeyRule<PrincipalT> implements AuthRule {

    @Override
    public SecretKeyAuthentication<PrincipalT> authenticate(HttpServletRequest request) throws UnauthorizedException, SecurityException {
        try {
            var secretKey = provideSecretKey();
            if (Arrays.equals(secretKey, extractSecretFromRequest(request))) {
                return new SecretKeyAuthentication<>(secretKey, providePrincipalInfo());
            }
        } catch (Exception e) {
            throw new SecurityException("Failed to authenticate using secret key");
        }

        throw new UnauthorizedException("Invalid secret key");
    }

    protected abstract byte[] extractSecretFromRequest(HttpServletRequest request);

    protected abstract byte[] provideSecretKey();

    protected abstract PrincipalT providePrincipalInfo();
}
