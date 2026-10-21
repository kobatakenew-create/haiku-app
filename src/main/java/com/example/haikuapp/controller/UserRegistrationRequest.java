package com.example.haikuapp.controller;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @NotBlank(message = "ログインIDを入力してください")
        @Size(max = 10, message = "ログインIDは10文字以内にしてください")
        @Pattern(
                regexp = "^[a-zA-Z0-9]+$",
                message = "ログインIDは半角英数字で入力してください")
        String loginId,

        @NotBlank(message = "パスワードを入力してください")
        @Size(
                min = 8,
                max = 10,
                message = "パスワードは8～10文字にしてください")
        @Pattern(
                regexp = "^[a-zA-Z0-9]+$",
                message = "パスワードは半角英数字で入力してください")
        String password
) {
}