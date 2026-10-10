package com.example.haikuapp.service.port;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RememberMeTokenPort {

    void save(
            Long userId,
            String tokenHash,
            LocalDateTime expiresAt
    );

    Optional<RememberMeTokenData> findByTokenHash(String tokenHash);

    void deleteByTokenHash(String tokenHash);

    void deleteExpiredTokens(LocalDateTime now);
}