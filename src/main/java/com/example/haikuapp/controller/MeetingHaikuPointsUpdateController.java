package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingHaikuPointsUpdateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
public class MeetingHaikuPointsUpdateController {

    private final MeetingHaikuPointsUpdateService updateService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingHaikuPointsUpdateController(
            MeetingHaikuPointsUpdateService updateService,
            SessionUserProvider sessionUserProvider
    ) {
        this.updateService = updateService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @PutMapping("/{meetingId}/haikus/{meetingHaikuId}/points")
    public ResponseEntity<Void> updatePoints(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("meetingHaikuId") Long meetingHaikuId,
            @RequestBody MeetingHaikuPointsUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        updateService.updatePoints(
                userId,
                meetingId,
                meetingHaikuId,
                request.points()
        );

        return ResponseEntity.noContent().build();
    }
}