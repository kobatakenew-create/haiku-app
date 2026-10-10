package com.example.haikuapp.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(

        @NotBlank(message = "ログインIDを入力してください")
        @Size(max = 10, message = "ログインIDは10文字以内にしてください")
        String loginId,

        @NotBlank(message = "パスワードを入力してください")
        @Size(max = 10, message = "パスワードは10文字以内にしてください")
        String password,

        boolean rememberMe
) {

    @Override
    public String toString() {
        return "UserLoginRequest[loginId=" + loginId
                + ", rememberMe=" + rememberMe + "]";
    }
}