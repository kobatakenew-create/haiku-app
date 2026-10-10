package com.example.haikuapp.controller;

import java.time.LocalDate;

public record MeetingCreationRequest(
        String meetingName,
        LocalDate meetingDate,
        String theme
) {
}