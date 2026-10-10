package com.example.haikuapp.service.port;

import java.time.LocalDateTime;

public record HaikuSummary(
        Long haikuId,
        String text,
        String season,
        String status,
        String selfEvaluation,
        Integer points,
        String memo,
        LocalDateTime createdAt
) {
}