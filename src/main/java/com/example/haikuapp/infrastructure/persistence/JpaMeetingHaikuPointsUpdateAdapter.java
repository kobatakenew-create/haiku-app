package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.MeetingHaikuRepository;
import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.MeetingNotFoundException;
import com.example.haikuapp.service.port.MeetingHaikuPointsUpdatePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaMeetingHaikuPointsUpdateAdapter
        implements MeetingHaikuPointsUpdatePort {

    private final MeetingRepository meetingRepository;
    private final MeetingHaikuRepository meetingHaikuRepository;

    public JpaMeetingHaikuPointsUpdateAdapter(
            MeetingRepository meetingRepository,
            MeetingHaikuRepository meetingHaikuRepository
    ) {
        this.meetingRepository = meetingRepository;
        this.meetingHaikuRepository = meetingHaikuRepository;
    }

    @Override
    @Transactional
    public boolean updatePoints(
            Long userId,
            Long meetingId,
            Long meetingHaikuId,
            Integer points
    ) {
        meetingRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(MeetingNotFoundException::new);

        return meetingHaikuRepository
                .findByMeetingHaikuIdAndMeetingId(
                        meetingHaikuId,
                        meetingId
                )
                .map(meetingHaiku -> {
                    meetingHaiku.updatePoints(points);
                    return true;
                })
                .orElse(false);
    }
}