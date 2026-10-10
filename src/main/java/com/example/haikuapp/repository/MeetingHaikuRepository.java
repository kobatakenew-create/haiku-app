package com.example.haikuapp.repository;

import com.example.haikuapp.entity.MeetingHaiku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingHaikuRepository
        extends JpaRepository<MeetingHaiku, Long> {

    boolean existsByMeetingIdAndHaikuId(
            Long meetingId,
            Long haikuId
    );

    List<MeetingHaiku> findByMeetingIdOrderByMeetingHaikuIdDesc(
            Long meetingId
    );

    Optional<MeetingHaiku> findByMeetingHaikuIdAndMeetingId(
            Long meetingHaikuId,
            Long meetingId
    );
}