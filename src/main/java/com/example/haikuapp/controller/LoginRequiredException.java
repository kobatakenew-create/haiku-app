package com.example.haikuapp.controller;

public class LoginRequiredException extends RuntimeException {

    public LoginRequiredException() {
        super("ログインしてください");
    }
}