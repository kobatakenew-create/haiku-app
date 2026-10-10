package com.example.haikuapp.service.port;

public interface UserRegistrationPort {

    boolean existsByLoginId(String loginId);

    Long save(String loginId, String passwordHash);
}