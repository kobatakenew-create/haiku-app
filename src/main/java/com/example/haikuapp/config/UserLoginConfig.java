package com.example.haikuapp.config;

import com.example.haikuapp.service.UserLoginService;
import com.example.haikuapp.service.port.PasswordHasher;
import com.example.haikuapp.service.port.UserLoginPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserLoginConfig {

    @Bean
    public UserLoginService userLoginService(
            UserLoginPort userLoginPort,
            PasswordHasher passwordHasher) {
        return new UserLoginService(
                userLoginPort,
                passwordHasher);
    }
}