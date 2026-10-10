package com.example.haikuapp.controller;

import java.time.LocalDateTime;

public record HaikuListResponse(
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