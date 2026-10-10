package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingUpdateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
public class MeetingUpdateController {

    private final MeetingUpdateService updateService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingUpdateController(
            MeetingUpdateService updateService,
            SessionUserProvider sessionUserProvider
    ) {
        this.updateService = updateService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<Void> update(
            @PathVariable("meetingId") Long meetingId,
            @RequestBody MeetingUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        updateService.update(
                userId,
                meetingId,
                request.meetingName(),
                request.meetingDate(),
                request.theme()
        );

        return ResponseEntity.noContent().build();
    }
}