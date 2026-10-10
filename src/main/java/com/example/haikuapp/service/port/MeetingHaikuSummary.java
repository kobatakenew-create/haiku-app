package com.example.haikuapp.service.port;

public record MeetingHaikuSummary(
        Long meetingHaikuId,
        Long haikuId,
        String text,
        String season,
        Integer points
) {
}