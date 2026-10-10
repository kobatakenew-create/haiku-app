package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingHaikuDeleteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
public class MeetingHaikuDeleteController {

    private final MeetingHaikuDeleteService deleteService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingHaikuDeleteController(
            MeetingHaikuDeleteService deleteService,
            SessionUserProvider sessionUserProvider
    ) {
        this.deleteService = deleteService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @DeleteMapping("/{meetingId}/haikus/{meetingHaikuId}")
    public ResponseEntity<Void> delete(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("meetingHaikuId") Long meetingHaikuId,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        deleteService.delete(
                userId,
                meetingId,
                meetingHaikuId
        );

        return ResponseEntity.noContent().build();
    }
}