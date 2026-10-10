package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingHaikuRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
public class MeetingHaikuRegistrationController {

    private final MeetingHaikuRegistrationService registrationService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingHaikuRegistrationController(
            MeetingHaikuRegistrationService registrationService,
            SessionUserProvider sessionUserProvider
    ) {
        this.registrationService = registrationService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @PostMapping("/{meetingId}/haikus")
    public ResponseEntity<MeetingHaikuRegistrationResponse> add(
            @PathVariable("meetingId") Long meetingId,
            @RequestBody MeetingHaikuRegistrationRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        Long meetingHaikuId = registrationService.add(
                userId,
                meetingId,
                request.haikuId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MeetingHaikuRegistrationResponse(meetingHaikuId));
    }
}