package com.example.haikuapp.service.port;

public interface HaikuCreationPort {

    Long save(
            Long userId,
            String text,
            String season,
            String status,
            String memo);
}