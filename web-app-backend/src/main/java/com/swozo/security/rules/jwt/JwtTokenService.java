package com.swozo.security.rules.jwt;

import com.swozo.config.EnvNames;
import com.swozo.persistence.user.Role;
import com.swozo.persistence.user.User;
import com.swozo.security.AccessToken;
import com.swozo.security.TokenService;
import com.swozo.security.keys.KeyProvider;
import com.swozo.security.util.AuthUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private static final String EXPIRY_DATE_FIELD = "exp";
    private static final String SUBJECT_FIELD = "sub";
    private static final String ISSUED_AT_FIELD = "iat";
    private static final String ROLES_FIELD = "rol";

    private final KeyProvider keyProvider;

    @Value("${" + EnvNames.JWT_DEFAULT_EXPIRATION_SECONDS + "}")
    private final long jwtExpirationTimeSeconds;
    @Value("${" + EnvNames.REFRESH_TOKEN_EXPIRATION_SECONDS + "}")
    private final long refreshTokenExpirationTimeSeconds;

    @Override
    public JwtAccessToken createAccessToken(User user) {
        var expirationTime = getExpirationTime(jwtExpirationTimeSeconds);
        var roles = user.getRoles().stream().map(Role::getName).toList();
        var token = createToken(String.valueOf(user.getId()), expirationTime, roles);

        return new JwtAccessToken(
                token,
                user.getId(),
                expirationTime.toInstant().getEpochSecond(),
                AuthUtils.getUsersAuthorities(user));
    }

    @Override
    public AccessToken createRefreshToken(User user) {
        var expirationTime = getExpirationTime(refreshTokenExpirationTimeSeconds);
        var token = createToken(String.valueOf(user.getId()), expirationTime, List.of());
        return new JwtAccessToken(token, user.getId(), expirationTime.toInstant().getEpochSecond(), List.of());
    }

    @Override
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

    @Override
    public Duration getRefreshTokenExpirationTime() {
        return Duration.ofSeconds(refreshTokenExpirationTimeSeconds);
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

    private Date getExpirationTime(long afterSeconds) {
        return Date.from(
                Instant.ofEpochSecond(
                        new Date().toInstant().getEpochSecond() + afterSeconds
                )
        );
    }
}
