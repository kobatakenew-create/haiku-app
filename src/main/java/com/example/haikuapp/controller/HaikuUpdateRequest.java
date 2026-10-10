package com.example.haikuapp.controller;

public record HaikuUpdateRequest(
        String text,
        String season,
        String status,
        String selfEvaluation,
        Integer points,
        String memo
) {
}