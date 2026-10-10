package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingHaikuListService;
import com.example.haikuapp.service.port.MeetingHaikuListPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingHaikuListConfig {

    @Bean
    public MeetingHaikuListService meetingHaikuListService(
            MeetingHaikuListPort listPort
    ) {
        return new MeetingHaikuListService(listPort);
    }
}