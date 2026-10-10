package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingCreationPort;

import java.time.LocalDate;

public class MeetingCreationService {

    private final MeetingCreationPort meetingCreationPort;

    public MeetingCreationService(
            MeetingCreationPort meetingCreationPort
    ) {
        this.meetingCreationPort = meetingCreationPort;
    }

    public Long create(
            Long userId,
            String meetingName,
            LocalDate meetingDate,
            String theme
    ) {
        String normalizedName = MeetingValidator.normalizeName(
                meetingName
        );

        MeetingValidator.validateDate(meetingDate);

        String normalizedTheme = MeetingValidator.normalizeTheme(
                theme
        );

        return meetingCreationPort.save(
                userId,
                normalizedName,
                meetingDate,
                normalizedTheme
        );
    }
}