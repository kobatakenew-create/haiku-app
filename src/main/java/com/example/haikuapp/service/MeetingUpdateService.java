package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingUpdatePort;

import java.time.LocalDate;

public class MeetingUpdateService {

    private final MeetingUpdatePort updatePort;

    public MeetingUpdateService(
            MeetingUpdatePort updatePort
    ) {
        this.updatePort = updatePort;
    }

    public void update(
            Long userId,
            Long meetingId,
            String meetingName,
            LocalDate meetingDate,
            String theme
    ) {
        if (meetingId == null || meetingId <= 0) {
            throw new MeetingNotFoundException();
        }

        String normalizedName = MeetingValidator.normalizeName(
                meetingName
        );

        MeetingValidator.validateDate(meetingDate);

        String normalizedTheme = MeetingValidator.normalizeTheme(
                theme
        );

        boolean updated = updatePort.update(
                userId,
                meetingId,
                normalizedName,
                meetingDate,
                normalizedTheme
        );

        if (!updated) {
            throw new MeetingNotFoundException();
        }
    }
}