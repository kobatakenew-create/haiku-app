package com.example.haikuapp.service;

public class DuplicateLoginIdException extends RuntimeException {

    public DuplicateLoginIdException() {
        super("このログインIDは使用されています");
    }
}