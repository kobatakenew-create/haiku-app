package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.port.MeetingUpdatePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class JpaMeetingUpdateAdapter
        implements MeetingUpdatePort {

    private final MeetingRepository meetingRepository;

    public JpaMeetingUpdateAdapter(
            MeetingRepository meetingRepository
    ) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    @Transactional
    public boolean update(
            Long userId,
            Long meetingId,
            String meetingName,
            LocalDate meetingDate,
            String theme
    ) {
        return meetingRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .map(meeting -> {
                    meeting.update(
                            meetingName,
                            meetingDate,
                            theme
                    );
                    return true;
                })
                .orElse(false);
    }
}