package com.example.haikuapp.controller;

import com.example.haikuapp.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(
            UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping
    public ResponseEntity<UserRegistrationResponse> register(
            @Valid @RequestBody UserRegistrationRequest request) {

        Long userId = userRegistrationService.register(
                request.loginId(),
                request.password());

        UserRegistrationResponse response =
                new UserRegistrationResponse(userId, request.loginId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}