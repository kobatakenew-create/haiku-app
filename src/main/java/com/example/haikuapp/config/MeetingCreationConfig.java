package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingCreationService;
import com.example.haikuapp.service.port.MeetingCreationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingCreationConfig {

    @Bean
    public MeetingCreationService meetingCreationService(
            MeetingCreationPort meetingCreationPort
    ) {
        return new MeetingCreationService(meetingCreationPort);
    }
}