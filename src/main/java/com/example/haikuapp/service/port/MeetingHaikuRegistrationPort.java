package com.example.haikuapp.service.port;

public interface MeetingHaikuRegistrationPort {

    Long add(
            Long userId,
            Long meetingId,
            Long haikuId
    );
}