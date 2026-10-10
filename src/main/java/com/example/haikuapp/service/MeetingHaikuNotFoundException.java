package com.example.haikuapp.service;

public class MeetingHaikuNotFoundException
        extends RuntimeException {

    public MeetingHaikuNotFoundException() {
        super("この句会に登録された俳句が見つかりません。");
    }
}