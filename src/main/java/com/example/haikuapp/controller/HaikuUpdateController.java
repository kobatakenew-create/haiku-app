package com.example.haikuapp.controller;

import com.example.haikuapp.service.HaikuUpdateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/haikus")
public class HaikuUpdateController {

    private final HaikuUpdateService haikuUpdateService;
    private final SessionUserProvider sessionUserProvider;

    public HaikuUpdateController(
            HaikuUpdateService haikuUpdateService,
            SessionUserProvider sessionUserProvider
    ) {
        this.haikuUpdateService = haikuUpdateService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @PutMapping("/{haikuId}")
    public ResponseEntity<Void> update(
            @PathVariable("haikuId") Long haikuId,
            @RequestBody HaikuUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = sessionUserProvider.requireUserId(httpRequest);

        haikuUpdateService.update(
                userId,
                haikuId,
                request.text(),
                request.season(),
                request.status(),
                request.selfEvaluation(),
                request.points(),
                request.memo()
        );

        return ResponseEntity.noContent().build();
    }
}