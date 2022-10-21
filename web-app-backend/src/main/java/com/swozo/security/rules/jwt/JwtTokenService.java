package com.swozo.security.rules.jwt;

import com.swozo.config.EnvNames;
import com.swozo.persistence.Role;
import com.swozo.persistence.User;
import com.swozo.security.TokenService;
import com.swozo.security.keys.KeyProvider;
import com.swozo.security.util.AuthUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class JwtTokenService implements TokenService {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private static final String EXPIRY_DATE_FIELD = "exp";
    private static final String SUBJECT_FIELD = "sub";
    private static final String ISSUED_AT_FIELD = "iat";
    private static final String ROLES_FIELD = "rol";

    private final KeyProvider keyProvider;

    @Value("${" + EnvNames.JWT_DEFAULT_EXPIRATION_SECONDS + "}")
    private long jwtExpirationTimeSeconds;

    @Autowired
    public JwtTokenService(KeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    public JwtAccessToken createAccessToken(User user) {
        var now = new Date();

        var expirationDate = Date.from(
                Instant.ofEpochSecond(
                        now.toInstant().getEpochSecond() + jwtExpirationTimeSeconds
                )
        );

        var roles = user.getRoles().stream().map(Role::getName).toList();
        var token = createToken(String.valueOf(user.getId()), expirationDate, roles);

        return new JwtAccessToken(
                token,
                user.getId(),
                expirationDate.toInstant().getEpochSecond(),
                AuthUtils.getUsersAuthorities(user));
    }

    @SuppressWarnings("unchecked") // suppress List to List<String> cast warning
    public JwtAccessToken parseAccessToken(String token) {
        var claims = parseClaims(token);

        return new JwtAccessToken(
                token,
                Integer.parseInt(claims.getSubject()),
                claims.get(EXPIRY_DATE_FIELD, Date.class).toInstant().getEpochSecond(),
                AuthUtils.getUsersAuthorities((List<String>) claims.get(ROLES_FIELD, List.class))
        );
    }

    private String createToken(String uuid, Date expirationDate, List<String> roles) {
        var claims = new HashMap<String, Object>();
        claims.put(EXPIRY_DATE_FIELD, expirationDate.toInstant().getEpochSecond());
        claims.put(SUBJECT_FIELD, uuid);
        claims.put(ISSUED_AT_FIELD, new Date().toInstant().getEpochSecond());
        claims.put(ROLES_FIELD, roles);

        return Jwts.builder()
                .setSubject(uuid)
                .setClaims(claims)
                .signWith(getSigningKey(), SIGNATURE_ALGORITHM)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(keyProvider.getJwtSecretKey());
    }

    private Claims parseClaims(String token) {
        var parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();

        return parser.parseClaimsJws(token).getBody();
    }
}
