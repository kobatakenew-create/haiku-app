package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingDeleteService;
import com.example.haikuapp.service.port.MeetingDeletePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingDeleteConfig {

    @Bean
    public MeetingDeleteService meetingDeleteService(
            MeetingDeletePort deletePort
    ) {
        return new MeetingDeleteService(deletePort);
    }
}