package com.example.haikuapp.service.port;

public interface HaikuDeletePort {

    boolean delete(Long userId, Long haikuId);
}