package com.example.haikuapp.service;

public class InvalidMeetingException extends RuntimeException {

    public InvalidMeetingException(String message) {
        super(message);
    }
}