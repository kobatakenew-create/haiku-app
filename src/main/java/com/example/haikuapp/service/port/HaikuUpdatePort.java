package com.example.haikuapp.service.port;

public interface HaikuUpdatePort {

    boolean update(
            Long userId,
            Long haikuId,
            String text,
            String season,
            String status,
            String selfEvaluation,
            Integer points,
            String memo
    );
}