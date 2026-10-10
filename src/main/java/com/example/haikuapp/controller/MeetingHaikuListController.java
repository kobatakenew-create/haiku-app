package com.example.haikuapp.controller;

import com.example.haikuapp.service.MeetingHaikuListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingHaikuListController {

    private final MeetingHaikuListService listService;
    private final SessionUserProvider sessionUserProvider;

    public MeetingHaikuListController(
            MeetingHaikuListService listService,
            SessionUserProvider sessionUserProvider
    ) {
        this.listService = listService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @GetMapping("/{meetingId}/haikus")
    public List<MeetingHaikuListResponse> list(
            @PathVariable("meetingId") Long meetingId,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        return listService.list(userId, meetingId)
                .stream()
                .map(haiku -> new MeetingHaikuListResponse(
                        haiku.meetingHaikuId(),
                        haiku.haikuId(),
                        haiku.text(),
                        haiku.season(),
                        haiku.points()
                ))
                .toList();
    }
}