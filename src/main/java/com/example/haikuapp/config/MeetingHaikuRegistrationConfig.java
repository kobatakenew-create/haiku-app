package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingHaikuRegistrationService;
import com.example.haikuapp.service.port.MeetingHaikuRegistrationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingHaikuRegistrationConfig {

    @Bean
    public MeetingHaikuRegistrationService meetingHaikuRegistrationService(
            MeetingHaikuRegistrationPort registrationPort
    ) {
        return new MeetingHaikuRegistrationService(registrationPort);
    }
}