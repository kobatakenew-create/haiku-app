package com.example.haikuapp.service;

public class InvalidHaikuException extends RuntimeException {

    public InvalidHaikuException(String message) {
        super(message);
    }
}