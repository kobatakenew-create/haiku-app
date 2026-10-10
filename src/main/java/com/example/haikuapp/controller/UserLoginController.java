package com.example.haikuapp.controller;

import com.example.haikuapp.service.LoginResult;
import com.example.haikuapp.service.RememberMeService;
import com.example.haikuapp.service.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class UserLoginController {

    private final UserLoginService userLoginService;
    private final RememberMeService rememberMeService;
    private final RememberMeCookie rememberMeCookie;

    public UserLoginController(
            UserLoginService userLoginService,
            RememberMeService rememberMeService,
            RememberMeCookie rememberMeCookie
    ) {
        this.userLoginService = userLoginService;
        this.rememberMeService = rememberMeService;
        this.rememberMeCookie = rememberMeCookie;
    }

    @PostMapping
    public UserLoginResponse login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        LoginResult result = userLoginService.login(
                request.loginId(),
                request.password()
        );

        String previousToken = rememberMeCookie.read(httpRequest);
        rememberMeService.revoke(previousToken);

        HttpSession session = httpRequest.getSession();
        httpRequest.changeSessionId();
        session.setAttribute("userId", result.userId());

        if (request.rememberMe()) {
            String token = rememberMeService.issue(result.userId());
            rememberMeCookie.write(httpRequest, httpResponse, token);
        } else {
            rememberMeCookie.clear(httpRequest, httpResponse);
        }

        return new UserLoginResponse(
                result.userId(),
                result.loginId()
        );
    }
}