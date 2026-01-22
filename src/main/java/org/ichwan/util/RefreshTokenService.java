package org.ichwan.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ichwan.domain.RefreshToken;
import org.ichwan.repository.RefreshTokenRepository;
import org.ichwan.service.impl.UserServiceImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenService {

    @Inject
    private RefreshTokenRepository repository;

    @Inject
    private UserServiceImpl userService;

    public String generateNewToken(Long userId) {
        return userService.generateAccessToken(userService.finById(userId));
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(userId);
        refreshToken.setExpireAt(Instant.now().plus(30, ChronoUnit.DAYS));
        return refreshToken;
    }

    public Optional<RefreshToken> validateRefreshToken(String token) {
        RefreshToken byToken = repository.findByToken(token);
        if (byToken == null || byToken.getExpireAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(byToken);
    }

    public RefreshToken changeToken(RefreshToken prevToken) {
        repository.delete("userId", prevToken.getUserId());
        return createRefreshToken(prevToken.getUserId());
    }
}
