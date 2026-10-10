package com.example.haikuapp.controller;

import com.example.haikuapp.service.RememberMeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserSessionController {

    private final RememberMeService rememberMeService;
    private final RememberMeCookie rememberMeCookie;

    public UserSessionController(
            RememberMeService rememberMeService,
            RememberMeCookie rememberMeCookie
    ) {
        this.rememberMeService = rememberMeService;
        this.rememberMeCookie = rememberMeCookie;
    }

    @GetMapping("/api/session")
    public ResponseEntity<SessionResponse> currentSession(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HttpSession session = request.getSession(false);

        if (session != null
                && session.getAttribute("userId") instanceof Long userId) {
            return ResponseEntity.ok(new SessionResponse(userId));
        }

        String token = rememberMeCookie.read(request);
        Optional<Long> restoredUserId =
                rememberMeService.restoreUserId(token);

        if (restoredUserId.isEmpty()) {
            if (token != null) {
                rememberMeCookie.clear(request, response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        HttpSession restoredSession = request.getSession();
        request.changeSessionId();
        restoredSession.setAttribute("userId", restoredUserId.get());

        return ResponseEntity.ok(
                new SessionResponse(restoredUserId.get()));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = rememberMeCookie.read(request);
        rememberMeService.revoke(token);
        rememberMeCookie.clear(request, response);

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.noContent().build();
    }

    public record SessionResponse(Long userId) {
    }
}