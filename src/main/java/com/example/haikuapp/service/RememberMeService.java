package com.example.haikuapp.service;

import com.example.haikuapp.service.port.RememberMeTokenPort;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

public class RememberMeService {

    public static final Duration TOKEN_LIFETIME = Duration.ofDays(30);

    private final RememberMeTokenPort tokenPort;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public RememberMeService(
            RememberMeTokenPort tokenPort,
            Clock clock
    ) {
        this.tokenPort = tokenPort;
        this.clock = clock;
    }

    public String issue(Long userId) {
        LocalDateTime now = LocalDateTime.now(clock);

        tokenPort.deleteExpiredTokens(now);

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        tokenPort.save(
                userId,
                hash(token),
                now.plus(TOKEN_LIFETIME)
        );

        return token;
    }

    public Optional<Long> restoreUserId(String token) {
        if (!isValidFormat(token)) {
            return Optional.empty();
        }

        String tokenHash = hash(token);
        LocalDateTime now = LocalDateTime.now(clock);

        return tokenPort.findByTokenHash(tokenHash)
                .flatMap(data -> {
                    if (!data.expiresAt().isAfter(now)) {
                        tokenPort.deleteByTokenHash(tokenHash);
                        return Optional.empty();
                    }

                    return Optional.of(data.userId());
                });
    }

    public void revoke(String token) {
        if (isValidFormat(token)) {
            tokenPort.deleteByTokenHash(hash(token));
        }
    }

    private boolean isValidFormat(String token) {
        return token != null
                && token.matches("[A-Za-z0-9_-]{43}");
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashedBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "トークンのハッシュ化に失敗しました",
                    exception
            );
        }
    }
}