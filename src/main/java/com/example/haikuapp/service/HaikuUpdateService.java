package com.example.haikuapp.service;

import com.example.haikuapp.service.port.HaikuUpdatePort;

public class HaikuUpdateService {

    private final HaikuUpdatePort haikuUpdatePort;

    public HaikuUpdateService(HaikuUpdatePort haikuUpdatePort) {
        this.haikuUpdatePort = haikuUpdatePort;
    }

    public void update(
            Long userId,
            Long haikuId,
            String text,
            String season,
            String status,
            String selfEvaluation,
            Integer points,
            String memo
    ) {
        HaikuValidator.validate(text, season, status);
        HaikuValidator.validateSelfEvaluation(selfEvaluation);
        HaikuValidator.validatePoints(points);

        boolean updated = haikuUpdatePort.update(
                userId,
                haikuId,
                text,
                season,
                status,
                selfEvaluation,
                points,
                memo
        );

        if (!updated) {
            throw new HaikuNotFoundException();
        }
    }
}