package com.example.haikuapp.config;

import com.example.haikuapp.service.HaikuListService;
import com.example.haikuapp.service.port.HaikuListPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HaikuListConfig {

    @Bean
    public HaikuListService haikuListService(HaikuListPort haikuListPort) {
        return new HaikuListService(haikuListPort);
    }
}