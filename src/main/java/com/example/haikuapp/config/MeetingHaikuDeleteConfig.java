package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingHaikuDeleteService;
import com.example.haikuapp.service.port.MeetingHaikuDeletePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingHaikuDeleteConfig {

    @Bean
    public MeetingHaikuDeleteService meetingHaikuDeleteService(
            MeetingHaikuDeletePort deletePort
    ) {
        return new MeetingHaikuDeleteService(deletePort);
    }
}