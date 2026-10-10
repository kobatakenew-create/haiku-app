package com.example.haikuapp.controller;

import java.time.LocalDate;

public record MeetingListResponse(
        Long meetingId,
        String meetingName,
        LocalDate meetingDate,
        String theme
) {
}