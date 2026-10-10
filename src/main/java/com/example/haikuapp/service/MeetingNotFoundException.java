package com.example.haikuapp.service;

public class MeetingNotFoundException extends RuntimeException {

    public MeetingNotFoundException() {
        super("句会が見つかりません。");
    }
}