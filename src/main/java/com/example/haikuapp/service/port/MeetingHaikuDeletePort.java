package com.example.haikuapp.service.port;

public interface MeetingHaikuDeletePort {

    boolean delete(
            Long userId,
            Long meetingId,
            Long meetingHaikuId
    );
}