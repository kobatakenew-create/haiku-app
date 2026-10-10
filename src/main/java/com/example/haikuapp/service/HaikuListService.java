package com.example.haikuapp.service;

import com.example.haikuapp.service.port.HaikuListPort;
import com.example.haikuapp.service.port.HaikuSummary;

import java.util.List;

public class HaikuListService {

    private final HaikuListPort haikuListPort;

    public HaikuListService(HaikuListPort haikuListPort) {
        this.haikuListPort = haikuListPort;
    }

    public List<HaikuSummary> list(Long userId) {
        return haikuListPort.findByUserId(userId);
    }
}