package com.example.haikuapp.config;

import com.example.haikuapp.service.UserRegistrationService;
import com.example.haikuapp.service.port.PasswordHasher;
import com.example.haikuapp.service.port.UserRegistrationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRegistrationConfig {

    @Bean
    public UserRegistrationService userRegistrationService(
            UserRegistrationPort userRegistrationPort,
            PasswordHasher passwordHasher) {
        return new UserRegistrationService(
                userRegistrationPort,
                passwordHasher);
    }
}