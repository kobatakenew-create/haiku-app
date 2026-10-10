package com.example.haikuapp.service.port;

import java.time.LocalDate;

public record MeetingSummary(
        Long meetingId,
        String meetingName,
        LocalDate meetingDate,
        String theme
) {
}