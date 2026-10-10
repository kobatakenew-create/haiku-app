package com.example.haikuapp.controller;

import com.example.haikuapp.service.RememberMeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RememberMeCookie {

    private static final String COOKIE_NAME = "haiku_remember_me";

    public String read(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void write(
            HttpServletRequest request,
            HttpServletResponse response,
            String token
    ) {
        addCookie(
                request,
                response,
                token,
                RememberMeService.TOKEN_LIFETIME
        );
    }

    public void clear(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        addCookie(request, response, "", Duration.ZERO);
    }

    private void addCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String value,
            Duration maxAge
    ) {
        ResponseCookie cookie = ResponseCookie
                .from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(request.isSecure())
                .sameSite("Lax")
                .path("/api")
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}