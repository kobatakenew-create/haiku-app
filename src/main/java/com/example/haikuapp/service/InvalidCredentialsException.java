package com.example.haikuapp.service;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("ログインIDまたはパスワードが正しくありません");
    }
}