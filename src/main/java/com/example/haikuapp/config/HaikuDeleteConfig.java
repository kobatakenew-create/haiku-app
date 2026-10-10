package com.example.haikuapp.config;

import com.example.haikuapp.service.HaikuDeleteService;
import com.example.haikuapp.service.port.HaikuDeletePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HaikuDeleteConfig {

    @Bean
    public HaikuDeleteService haikuDeleteService(
            HaikuDeletePort haikuDeletePort
    ) {
        return new HaikuDeleteService(haikuDeletePort);
    }
}