package com.swozo.persistence.user;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "RefreshTokens", indexes = {
        @Index(name = "idx_refreshtokenentity", columnList = "issuedAt"),
        @Index(name = "idx_refreshtokenentity_userid", columnList = "userId")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RefreshTokenEntity extends BaseEntity {
    @Column(columnDefinition="TEXT")
    private String token;
    private Long userId;
    private LocalDateTime issuedAt = LocalDateTime.now();
}
