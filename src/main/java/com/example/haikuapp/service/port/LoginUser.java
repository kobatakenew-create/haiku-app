package com.example.haikuapp.service.port;

public record LoginUser(
        Long userId,
        String loginId,
        String passwordHash
) {
    @Override
    public String toString() {
        return "LoginUser[userId=" + userId
                + ", loginId=" + loginId + "]";
    }
}