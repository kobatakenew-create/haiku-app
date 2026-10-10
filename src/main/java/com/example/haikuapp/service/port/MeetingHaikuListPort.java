package com.example.haikuapp.service.port;

import java.util.List;

public interface MeetingHaikuListPort {

    List<MeetingHaikuSummary> findByMeeting(
            Long userId,
            Long meetingId
    );
}