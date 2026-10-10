package com.example.haikuapp.controller;

import java.time.LocalDate;

public record MeetingUpdateRequest(
        String meetingName,
        LocalDate meetingDate,
        String theme
) {
}