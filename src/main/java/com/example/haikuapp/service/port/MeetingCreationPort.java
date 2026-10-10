package com.example.haikuapp.service.port;

import java.time.LocalDate;

public interface MeetingCreationPort {

    Long save(
            Long userId,
            String meetingName,
            LocalDate meetingDate,
            String theme
    );
}