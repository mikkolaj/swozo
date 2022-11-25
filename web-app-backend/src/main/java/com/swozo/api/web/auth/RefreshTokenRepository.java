package com.swozo.api.web.auth;

import com.swozo.persistence.user.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    void deleteAllByUserId(Long userId);

    void deleteAllByIssuedAtBefore(LocalDateTime dateTime);

    Optional<RefreshTokenEntity> findByUserIdAndIssuedAtAfter(Long userId, LocalDateTime dateTime);
}
