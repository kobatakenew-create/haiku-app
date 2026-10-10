package com.example.haikuapp.controller;

import com.example.haikuapp.service.HaikuDeleteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/haikus")
public class HaikuDeleteController {

    private final HaikuDeleteService haikuDeleteService;
    private final SessionUserProvider sessionUserProvider;

    public HaikuDeleteController(
            HaikuDeleteService haikuDeleteService,
            SessionUserProvider sessionUserProvider
    ) {
        this.haikuDeleteService = haikuDeleteService;
        this.sessionUserProvider = sessionUserProvider;
    }

    @DeleteMapping("/{haikuId}")
    public ResponseEntity<Void> delete(
            @PathVariable("haikuId") Long haikuId,
            HttpServletRequest request
    ) {
        Long userId = sessionUserProvider.requireUserId(request);

        haikuDeleteService.delete(userId, haikuId);

        return ResponseEntity.noContent().build();
    }
}