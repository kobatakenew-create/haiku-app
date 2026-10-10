package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingUpdateService;
import com.example.haikuapp.service.port.MeetingUpdatePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingUpdateConfig {

    @Bean
    public MeetingUpdateService meetingUpdateService(
            MeetingUpdatePort updatePort
    ) {
        return new MeetingUpdateService(updatePort);
    }
}