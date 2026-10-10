package com.example.haikuapp.service;

import com.example.haikuapp.service.port.PasswordHasher;
import com.example.haikuapp.service.port.UserRegistrationPort;

public class UserRegistrationService {

    private final UserRegistrationPort userRegistrationPort;
    private final PasswordHasher passwordHasher;

    public UserRegistrationService(
            UserRegistrationPort userRegistrationPort,
            PasswordHasher passwordHasher) {
        this.userRegistrationPort = userRegistrationPort;
        this.passwordHasher = passwordHasher;
    }

    public Long register(String loginId, String rawPassword) {
        if (userRegistrationPort.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException();
        }

        String passwordHash = passwordHasher.hash(rawPassword);

        return userRegistrationPort.save(loginId, passwordHash);
    }
}