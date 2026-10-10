package com.example.haikuapp.config;

import com.example.haikuapp.service.HaikuCreationService;
import com.example.haikuapp.service.port.HaikuCreationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HaikuCreationConfig {

    @Bean
    public HaikuCreationService haikuCreationService(
            HaikuCreationPort haikuCreationPort) {
        return new HaikuCreationService(haikuCreationPort);
    }
}