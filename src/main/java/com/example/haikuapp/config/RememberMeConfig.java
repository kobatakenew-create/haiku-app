package com.example.haikuapp.config;

import com.example.haikuapp.service.RememberMeService;
import com.example.haikuapp.service.port.RememberMeTokenPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class RememberMeConfig {

    @Bean
    public RememberMeService rememberMeService(
            RememberMeTokenPort tokenPort
    ) {
        return new RememberMeService(tokenPort, Clock.systemUTC());
    }
}