package com.example.haikuapp.config;

import com.example.haikuapp.service.MeetingListService;
import com.example.haikuapp.service.port.MeetingListPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingListConfig {

    @Bean
    public MeetingListService meetingListService(
            MeetingListPort meetingListPort
    ) {
        return new MeetingListService(meetingListPort);
    }
}