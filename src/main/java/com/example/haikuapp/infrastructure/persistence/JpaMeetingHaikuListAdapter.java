package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.entity.Haiku;
import com.example.haikuapp.entity.MeetingHaiku;
import com.example.haikuapp.repository.HaikuRepository;
import com.example.haikuapp.repository.MeetingHaikuRepository;
import com.example.haikuapp.repository.MeetingRepository;
import com.example.haikuapp.service.HaikuNotFoundException;
import com.example.haikuapp.service.MeetingNotFoundException;
import com.example.haikuapp.service.port.MeetingHaikuListPort;
import com.example.haikuapp.service.port.MeetingHaikuSummary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JpaMeetingHaikuListAdapter
        implements MeetingHaikuListPort {

    private final MeetingRepository meetingRepository;
    private final MeetingHaikuRepository meetingHaikuRepository;
    private final HaikuRepository haikuRepository;

    public JpaMeetingHaikuListAdapter(
            MeetingRepository meetingRepository,
            MeetingHaikuRepository meetingHaikuRepository,
            HaikuRepository haikuRepository
    ) {
        this.meetingRepository = meetingRepository;
        this.meetingHaikuRepository = meetingHaikuRepository;
        this.haikuRepository = haikuRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingHaikuSummary> findByMeeting(
            Long userId,
            Long meetingId
    ) {
        meetingRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(MeetingNotFoundException::new);

        List<MeetingHaiku> meetingHaikus = meetingHaikuRepository
                .findByMeetingIdOrderByMeetingHaikuIdDesc(meetingId);

        if (meetingHaikus.isEmpty()) {
            return List.of();
        }

        List<Long> haikuIds = meetingHaikus.stream()
                .map(MeetingHaiku::getHaikuId)
                .toList();

        Map<Long, Haiku> haikusById = haikuRepository
                .findAllById(haikuIds)
                .stream()
                .filter(haiku -> userId.equals(haiku.getUserId()))
                .collect(Collectors.toMap(
                        Haiku::getHaikuId,
                        haiku -> haiku
                ));

        return meetingHaikus.stream()
                .map(meetingHaiku -> {
                    Haiku haiku = haikusById.get(
                            meetingHaiku.getHaikuId()
                    );

                    if (haiku == null) {
                        throw new HaikuNotFoundException();
                    }

                    return new MeetingHaikuSummary(
                            meetingHaiku.getMeetingHaikuId(),
                            haiku.getHaikuId(),
                            haiku.getText(),
                            haiku.getSeason(),
                            meetingHaiku.getPoints()
                    );
                })
                .toList();
    }
}