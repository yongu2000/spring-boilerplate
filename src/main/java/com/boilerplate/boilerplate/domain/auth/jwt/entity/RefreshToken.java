package com.boilerplate.boilerplate.domain.auth.jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "refresh_token", nullable = false, length = 2048)
    private String refreshToken;

    private LocalDateTime expiration;

    @Builder
    public RefreshToken(Long userId, String refreshToken, LocalDateTime expiration) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public void update(String newRefreshToken, LocalDateTime expiration) {
        this.refreshToken = newRefreshToken;
        this.expiration = expiration;
    }
}
