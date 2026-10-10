package com.example.haikuapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionUserProvider {

    public Long requireUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new LoginRequiredException();
        }

        Object userId = session.getAttribute("userId");

        if (!(userId instanceof Long)) {
            throw new LoginRequiredException();
        }

        return (Long) userId;
    }
}