package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingHaikuRegistrationPort;

public class MeetingHaikuRegistrationService {

    private final MeetingHaikuRegistrationPort registrationPort;

    public MeetingHaikuRegistrationService(
            MeetingHaikuRegistrationPort registrationPort
    ) {
        this.registrationPort = registrationPort;
    }

    public Long add(
            Long userId,
            Long meetingId,
            Long haikuId
    ) {
        if (meetingId == null || meetingId <= 0) {
            throw new MeetingNotFoundException();
        }

        if (haikuId == null || haikuId <= 0) {
            throw new HaikuNotFoundException();
        }

        return registrationPort.add(userId, meetingId, haikuId);
    }
}