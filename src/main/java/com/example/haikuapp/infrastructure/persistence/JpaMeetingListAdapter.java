package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.port.MeetingListPort;
import com.example.haikuapp.service.port.MeetingSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaMeetingListAdapter implements MeetingListPort {

    private final MeetingRepository meetingRepository;

    public JpaMeetingListAdapter(
            MeetingRepository meetingRepository
    ) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    public List<MeetingSummary> findByUserId(Long userId) {
        return meetingRepository
                .findByUserIdOrderByMeetingDateDescMeetingIdDesc(userId)
                .stream()
                .map(meeting -> new MeetingSummary(
                        meeting.getMeetingId(),
                        meeting.getMeetingName(),
                        meeting.getMeetingDate(),
                        meeting.getTheme()
                ))
                .toList();
    }
}