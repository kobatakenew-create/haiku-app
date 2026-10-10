package com.example.haikuapp.service;

import com.example.haikuapp.service.port.HaikuDeletePort;

public class HaikuDeleteService {

    private final HaikuDeletePort haikuDeletePort;

    public HaikuDeleteService(HaikuDeletePort haikuDeletePort) {
        this.haikuDeletePort = haikuDeletePort;
    }

    public void delete(Long userId, Long haikuId) {
        boolean deleted = haikuDeletePort.delete(userId, haikuId);

        if (!deleted) {
            throw new HaikuNotFoundException();
        }
    }
}