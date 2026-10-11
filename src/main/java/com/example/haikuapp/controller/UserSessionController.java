package com.example.haikuapp.controller;

import com.example.haikuapp.service.LoginResult;
import com.example.haikuapp.service.RememberMeService;
import com.example.haikuapp.service.UserLoginService;
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
    private final UserLoginService userLoginService;

    public UserSessionController(
            RememberMeService rememberMeService,
            RememberMeCookie rememberMeCookie,
            UserLoginService userLoginService
    ) {
        this.rememberMeService = rememberMeService;
        this.rememberMeCookie = rememberMeCookie;
        this.userLoginService = userLoginService;
    }

    @GetMapping("/api/session")
    public ResponseEntity<SessionResponse> currentSession(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HttpSession session = request.getSession(false);

        if (session != null
                && session.getAttribute("userId") instanceof Long userId) {
            Optional<LoginResult> user = userLoginService.findCurrentUser(userId);
            if (user.isPresent()) {
                return ResponseEntity.ok(toResponse(user.get()));
            }

            session.invalidate();
            rememberMeCookie.clear(request, response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = rememberMeCookie.read(request);
        Optional<LoginResult> restoredUser = rememberMeService.restoreUserId(token)
                .flatMap(userLoginService::findCurrentUser);

        if (restoredUser.isEmpty()) {
            if (token != null) {
                rememberMeCookie.clear(request, response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        HttpSession restoredSession = request.getSession();
        request.changeSessionId();
        restoredSession.setAttribute("userId", restoredUser.get().userId());

        return ResponseEntity.ok(toResponse(restoredUser.get()));
    }

    private SessionResponse toResponse(LoginResult user) {
        return new SessionResponse(user.userId(), user.loginId());
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

    public record SessionResponse(Long userId, String loginId) {
    }
}
