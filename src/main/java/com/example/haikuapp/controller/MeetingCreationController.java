package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingCreationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
public class MeetingCreationController {

    private final MeetingCreationService meetingCreationService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingCreationController(
            MeetingCreationService meetingCreationService,
            SessionUserProvider sessionUserProvider
    ) {
        this.meetingCreationService = meetingCreationService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @PostMapping
    public ResponseEntity<MeetingCreationResponse> create(
            @RequestBody MeetingCreationRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        Long meetingId = meetingCreationService.create(
                userId,
                request.meetingName(),
                request.meetingDate(),
                request.theme()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MeetingCreationResponse(meetingId));
    }
}