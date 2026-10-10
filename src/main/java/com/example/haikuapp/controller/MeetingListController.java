package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingListController {

    private final MeetingListService meetingListService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingListController(
            MeetingListService meetingListService,
            SessionUserProvider sessionUserProvider
    ) {
        this.meetingListService = meetingListService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @GetMapping
    public List<MeetingListResponse> list(
            HttpServletRequest request
    ) {
        Long userId = sessionUserProvider.requireUserId(request);

        return meetingListService.list(userId)
                .stream()
                .map(meeting -> new MeetingListResponse(
                        meeting.meetingId(),
                        meeting.meetingName(),
                        meeting.meetingDate(),
                        meeting.theme()
                ))
                .toList();
    }
}