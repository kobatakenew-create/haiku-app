package com.example.haikuapp.service.port;

import java.time.LocalDate;

public interface MeetingUpdatePort {

    boolean update(
            Long userId,
            Long meetingId,
            String meetingName,
            LocalDate meetingDate,
            String theme
    );
}