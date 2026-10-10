package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingHaikuListPort;
import com.example.haikuapp.service.port.MeetingHaikuSummary;

import java.util.List;

public class MeetingHaikuListService {

    private final MeetingHaikuListPort listPort;

    public MeetingHaikuListService(
            MeetingHaikuListPort listPort
    ) {
        this.listPort = listPort;
    }

    public List<MeetingHaikuSummary> list(
            Long userId,
            Long meetingId
    ) {
        if (meetingId == null || meetingId <= 0) {
            throw new MeetingNotFoundException();
        }

        return listPort.findByMeeting(userId, meetingId);
    }
}