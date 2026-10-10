package com.example.haikuapp.service;

import com.example.haikuapp.service.port.HaikuCreationPort;

public class HaikuCreationService {

    private final HaikuCreationPort haikuCreationPort;

    public HaikuCreationService(HaikuCreationPort haikuCreationPort) {
        this.haikuCreationPort = haikuCreationPort;
    }

    public Long create(
            Long userId,
            String text,
            String season,
            String status,
            String memo
    ) {
        HaikuValidator.validate(text, season, status);

        return haikuCreationPort.save(
                userId, text, season, status, memo);
    }
}