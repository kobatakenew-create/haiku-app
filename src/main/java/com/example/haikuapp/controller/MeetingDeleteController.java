package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingDeleteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
public class MeetingDeleteController {

    private final MeetingDeleteService deleteService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingDeleteController(
            MeetingDeleteService deleteService,
            SessionUserProvider sessionUserProvider
    ) {
        this.deleteService = deleteService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> delete(
            @PathVariable("meetingId") Long meetingId,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        deleteService.delete(
                userId,
                meetingId
        );

        return ResponseEntity.noContent().build();
    }
}