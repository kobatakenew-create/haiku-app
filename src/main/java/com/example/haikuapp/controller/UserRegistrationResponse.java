package com.example.haikuapp.controller;

public record UserRegistrationResponse(
        Long userId,
        String loginId
) {
}