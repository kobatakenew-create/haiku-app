package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingHaikuPointsUpdatePort;

public class MeetingHaikuPointsUpdateService {

    private final MeetingHaikuPointsUpdatePort updatePort;

    public MeetingHaikuPointsUpdateService(
            MeetingHaikuPointsUpdatePort updatePort
    ) {
        this.updatePort = updatePort;
    }

    public void updatePoints(
            Long userId,
            Long meetingId,
            Long meetingHaikuId,
            Integer points
    ) {
        if (meetingId == null || meetingId <= 0) {
            throw new MeetingNotFoundException();
        }

        if (meetingHaikuId == null || meetingHaikuId <= 0) {
            throw new MeetingHaikuNotFoundException();
        }

        HaikuValidator.validatePoints(points);

        boolean updated = updatePort.updatePoints(
                userId,
                meetingId,
                meetingHaikuId,
                points
        );

        if (!updated) {
            throw new MeetingHaikuNotFoundException();
        }
    }
}