package com.example.haikuapp.service;

public class HaikuNotFoundException extends RuntimeException {

    public HaikuNotFoundException() {
        super("俳句が見つかりません。");
    }
}