package com.example.haikuapp.service.port;

import java.util.List;

public interface MeetingListPort {

    List<MeetingSummary> findByUserId(Long userId);
}