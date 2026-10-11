package com.example.haikuapp.controller;

import com.example.haikuapp.service.LoginResult;
import com.example.haikuapp.service.RememberMeService;
import com.example.haikuapp.service.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSessionControllerTest {
    private final RememberMeService rememberMeService = mock(RememberMeService.class);
    private final RememberMeCookie cookie = mock(RememberMeCookie.class);
    private final UserLoginService loginService = mock(UserLoginService.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final UserSessionController controller =
            new UserSessionController(rememberMeService, cookie, loginService);

    @Test
    void existingSessionReturnsLoginId() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(loginService.findCurrentUser(1L)).thenReturn(Optional.of(new LoginResult(1L, "GAKU")));

        var result = controller.currentSession(request, response);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("GAKU", result.getBody().loginId());
        assertEquals(1L, result.getBody().userId());
    }

    @Test
    void rememberMeRestoresLoginIdAndSession() {
        HttpSession session = mock(HttpSession.class);
        when(cookie.read(request)).thenReturn("test-token");
        when(rememberMeService.restoreUserId("test-token")).thenReturn(Optional.of(2L));
        when(loginService.findCurrentUser(2L)).thenReturn(Optional.of(new LoginResult(2L, "OTHER")));
        when(request.getSession()).thenReturn(session);

        var result = controller.currentSession(request, response);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("OTHER", result.getBody().loginId());
        verify(request).changeSessionId();
        verify(session).setAttribute("userId", 2L);
    }

    @Test
    void missingUserInvalidatesExistingSession() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3L);
        when(loginService.findCurrentUser(3L)).thenReturn(Optional.empty());

        var result = controller.currentSession(request, response);

        assertEquals(401, result.getStatusCode().value());
        verify(session).invalidate();
        verify(cookie).clear(request, response);
    }

    @Test
    void missingRememberedUserDoesNotCreateSession() {
        when(cookie.read(request)).thenReturn("old-token");
        when(rememberMeService.restoreUserId("old-token")).thenReturn(Optional.of(4L));
        when(loginService.findCurrentUser(4L)).thenReturn(Optional.empty());

        var result = controller.currentSession(request, response);

        assertEquals(401, result.getStatusCode().value());
        verify(request, never()).getSession();
        verify(cookie).clear(request, response);
    }

    @Test
    void unauthenticatedRequestReturnsUnauthorized() {
        when(rememberMeService.restoreUserId(null)).thenReturn(Optional.empty());

        var result = controller.currentSession(request, response);

        assertEquals(401, result.getStatusCode().value());
        verifyNoInteractions(loginService);
    }
}
