package com.example.haikuapp.service.port;

public interface MeetingHaikuPointsUpdatePort {

    boolean updatePoints(
            Long userId,
            Long meetingId,
            Long meetingHaikuId,
            Integer points
    );
}