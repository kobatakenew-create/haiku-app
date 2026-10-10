package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.MeetingHaikuRepository;
import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.MeetingNotFoundException;
import com.example.haikuapp.service.port.MeetingHaikuDeletePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaMeetingHaikuDeleteAdapter
        implements MeetingHaikuDeletePort {

    private final MeetingRepository meetingRepository;
    private final MeetingHaikuRepository meetingHaikuRepository;

    public JpaMeetingHaikuDeleteAdapter(
            MeetingRepository meetingRepository,
            MeetingHaikuRepository meetingHaikuRepository
    ) {
        this.meetingRepository = meetingRepository;
        this.meetingHaikuRepository = meetingHaikuRepository;
    }

    @Override
    @Transactional
    public boolean delete(
            Long userId,
            Long meetingId,
            Long meetingHaikuId
    ) {
        meetingRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(MeetingNotFoundException::new);

        return meetingHaikuRepository
                .findByMeetingHaikuIdAndMeetingId(
                        meetingHaikuId,
                        meetingId
                )
                .map(meetingHaiku -> {
                    meetingHaikuRepository.delete(meetingHaiku);
                    return true;
                })
                .orElse(false);
    }
}