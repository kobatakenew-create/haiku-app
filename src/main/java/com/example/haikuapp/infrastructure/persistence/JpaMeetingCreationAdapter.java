package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.entity.Meeting;
import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.port.MeetingCreationPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class JpaMeetingCreationAdapter implements MeetingCreationPort {

    private final MeetingRepository meetingRepository;

    public JpaMeetingCreationAdapter(
            MeetingRepository meetingRepository
    ) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    public Long save(
            Long userId,
            String meetingName,
            LocalDate meetingDate,
            String theme
    ) {
        Meeting meeting = new Meeting(
                userId,
                meetingName,
                meetingDate,
                theme
        );

        return meetingRepository.save(meeting).getMeetingId();
    }
}