package com.example.haikuapp.service;

public class DuplicateMeetingHaikuException extends RuntimeException {

    public DuplicateMeetingHaikuException() {
        super("この俳句は、すでにこの句会に登録されています。");
    }
}