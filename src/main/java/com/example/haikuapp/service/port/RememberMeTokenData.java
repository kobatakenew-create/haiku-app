package com.example.haikuapp.service.port;

import java.time.LocalDateTime;

public record RememberMeTokenData(
        Long userId,
        LocalDateTime expiresAt
) {
}