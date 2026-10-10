package com.example.haikuapp.controller;

public record HaikuCreationRequest(
        String text,
        String season,
        String status,
        String memo
) {
}