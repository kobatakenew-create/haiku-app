package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.entity.MeetingHaiku;
import com.example.haikuapp.repository.HaikuRepository;
import com.example.haikuapp.repository.MeetingHaikuRepository;
import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.DuplicateMeetingHaikuException;
import com.example.haikuapp.service.HaikuNotFoundException;
import com.example.haikuapp.service.MeetingNotFoundException;
import com.example.haikuapp.service.port.MeetingHaikuRegistrationPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Component
public class JpaMeetingHaikuRegistrationAdapter
        implements MeetingHaikuRegistrationPort {

    private final MeetingRepository meetingRepository;
    private final HaikuRepository haikuRepository;
    private final MeetingHaikuRepository meetingHaikuRepository;

    public JpaMeetingHaikuRegistrationAdapter(
            MeetingRepository meetingRepository,
            HaikuRepository haikuRepository,
            MeetingHaikuRepository meetingHaikuRepository
    ) {
        this.meetingRepository = meetingRepository;
        this.haikuRepository = haikuRepository;
        this.meetingHaikuRepository = meetingHaikuRepository;
    }

    @Override
    @Transactional
    public Long add(
            Long userId,
            Long meetingId,
            Long haikuId
    ) {
        meetingRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(MeetingNotFoundException::new);

        haikuRepository.findByHaikuIdAndUserId(haikuId, userId)
                .orElseThrow(HaikuNotFoundException::new);

        if (meetingHaikuRepository.existsByMeetingIdAndHaikuId(
                meetingId, haikuId)) {
            throw new DuplicateMeetingHaikuException();
        }

        MeetingHaiku meetingHaiku = new MeetingHaiku(
                meetingId,
                haikuId
        );

        try {
            return meetingHaikuRepository
                    .saveAndFlush(meetingHaiku)
                    .getMeetingHaikuId();
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMostSpecificCause()
                    instanceof SQLException sqlException
                    && sqlException.getErrorCode() == 1062) {
                throw new DuplicateMeetingHaikuException();
            }

            throw exception;
        }
    }
}