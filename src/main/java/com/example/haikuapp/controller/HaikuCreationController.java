package com.example.haikuapp.controller;

import com.example.haikuapp.service.HaikuCreationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/haikus")
public class HaikuCreationController {

    private final HaikuCreationService haikuCreationService;
    private final SessionUserProvider sessionUserProvider;

    public HaikuCreationController(
            HaikuCreationService haikuCreationService,
            SessionUserProvider sessionUserProvider) {
        this.haikuCreationService = haikuCreationService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @PostMapping
    public ResponseEntity<HaikuCreationResponse> create(
            @RequestBody HaikuCreationRequest request,
            HttpServletRequest httpRequest) {

        Long userId = sessionUserProvider.requireUserId(httpRequest);

        Long haikuId = haikuCreationService.create(
                userId,
                request.text(),
                request.season(),
                request.status(),
                request.memo());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new HaikuCreationResponse(haikuId));
    }
}