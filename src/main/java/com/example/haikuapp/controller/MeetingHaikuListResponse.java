package com.example.haikuapp.controller;

public record MeetingHaikuListResponse(
        Long meetingHaikuId,
        Long haikuId,
        String text,
        String season,
        Integer points
) {
}