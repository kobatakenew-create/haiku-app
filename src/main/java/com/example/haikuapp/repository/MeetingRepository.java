package com.example.haikuapp.repository;

import com.example.haikuapp.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository
        extends JpaRepository<Meeting, Long> {

    List<Meeting> findByUserIdOrderByMeetingDateDescMeetingIdDesc(
            Long userId
    );

    Optional<Meeting> findByMeetingIdAndUserId(
            Long meetingId,
            Long userId
    );
}