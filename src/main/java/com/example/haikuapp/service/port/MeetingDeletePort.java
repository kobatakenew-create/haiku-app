package com.example.haikuapp.service.port;

public interface MeetingDeletePort {

    boolean delete(
            Long userId,
            Long meetingId
    );
}
