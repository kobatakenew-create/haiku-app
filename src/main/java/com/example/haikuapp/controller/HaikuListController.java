package com.example.haikuapp.controller;

import com.example.haikuapp.service.HaikuListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/haikus")
public class HaikuListController {

    private final HaikuListService haikuListService;
    private final SessionUserProvider sessionUserProvider;

    public HaikuListController(
            HaikuListService haikuListService,
            SessionUserProvider sessionUserProvider
    ) {
        this.haikuListService = haikuListService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @GetMapping
    public List<HaikuListResponse> list(HttpServletRequest request) {
        Long userId = sessionUserProvider.requireUserId(request);

        return haikuListService.list(userId)
                .stream()
                .map(haiku -> new HaikuListResponse(
                        haiku.haikuId(),
                        haiku.text(),
                        haiku.season(),
                        haiku.status(),
                        haiku.selfEvaluation(),
                        haiku.points(),
                        haiku.memo(),
                        haiku.createdAt()
                ))
                .toList();
    }
}