package com.example.haikuapp.controller;

public record UserLoginResponse(
        Long userId,
        String loginId
) {
}