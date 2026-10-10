package com.example.haikuapp.config;

import com.example.haikuapp.service.HaikuUpdateService;
import com.example.haikuapp.service.port.HaikuUpdatePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HaikuUpdateConfig {

    @Bean
    public HaikuUpdateService haikuUpdateService(
            HaikuUpdatePort haikuUpdatePort
    ) {
        return new HaikuUpdateService(haikuUpdatePort);
    }
}