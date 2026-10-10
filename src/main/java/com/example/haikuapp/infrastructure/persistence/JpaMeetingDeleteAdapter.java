package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.port.MeetingDeletePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaMeetingDeleteAdapter
        implements MeetingDeletePort {

    private final MeetingRepository meetingRepository;

    public JpaMeetingDeleteAdapter(
            MeetingRepository meetingRepository
    ) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    @Transactional
    public boolean delete(
            Long userId,
            Long meetingId
    ) {
        return meetingRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .map(meeting -> {
                    meetingRepository.delete(meeting);
                    return true;
                })
                .orElse(false);
    }
}