package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingHaikuDeletePort;

public class MeetingHaikuDeleteService {

    private final MeetingHaikuDeletePort deletePort;

    public MeetingHaikuDeleteService(
            MeetingHaikuDeletePort deletePort
    ) {
        this.deletePort = deletePort;
    }

    public void delete(
            Long userId,
            Long meetingId,
            Long meetingHaikuId
    ) {
        if (meetingId == null || meetingId <= 0) {
            throw new MeetingNotFoundException();
        }

        if (meetingHaikuId == null || meetingHaikuId <= 0) {
            throw new MeetingHaikuNotFoundException();
        }

        boolean deleted = deletePort.delete(
                userId,
                meetingId,
                meetingHaikuId
        );

        if (!deleted) {
            throw new MeetingHaikuNotFoundException();
        }
    }
}