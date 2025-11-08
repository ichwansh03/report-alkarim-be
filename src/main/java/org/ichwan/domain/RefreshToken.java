package org.ichwan.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends Auditable{

    @Column(unique = true, nullable = false)
    private String token;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Instant expireAt;

    public RefreshToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expireAt);
    }
}
