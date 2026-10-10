package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingHaikuPointsUpdateService;
import com.example.haikuapp.service.port.MeetingHaikuPointsUpdatePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingHaikuPointsUpdateConfig {

    @Bean
    public MeetingHaikuPointsUpdateService meetingHaikuPointsUpdateService(
            MeetingHaikuPointsUpdatePort updatePort
    ) {
        return new MeetingHaikuPointsUpdateService(updatePort);
    }
}