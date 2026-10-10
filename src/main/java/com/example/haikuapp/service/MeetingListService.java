package com.example.haikuapp.service;

import com.example.haikuapp.service.port.MeetingListPort;
import com.example.haikuapp.service.port.MeetingSummary;

import java.util.List;

public class MeetingListService {

    private final MeetingListPort meetingListPort;

    public MeetingListService(MeetingListPort meetingListPort) {
        this.meetingListPort = meetingListPort;
    }

    public List<MeetingSummary> list(Long userId) {
        return meetingListPort.findByUserId(userId);
    }
}