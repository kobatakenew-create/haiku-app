package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingDeletePort;

public class MeetingDeleteService {

    private final MeetingDeletePort deletePort;

    public MeetingDeleteService(
            MeetingDeletePort deletePort
    ) {
        this.deletePort = deletePort;
    }

    public void delete(
            Long userId,
            Long meetingId
    ) {
        if (meetingId == null || meetingId <= 0) {
            throw new MeetingNotFoundException();
        }

        boolean deleted = deletePort.delete(
                userId,
                meetingId
        );

        if (!deleted) {
            throw new MeetingNotFoundException();
        }
    }
}