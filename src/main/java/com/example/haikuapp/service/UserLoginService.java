package com.example.haikuapp.service;

import com.example.haikuapp.service.port.LoginUser;
import com.example.haikuapp.service.port.PasswordHasher;
import com.example.haikuapp.service.port.UserLoginPort;

import java.util.Optional;

public class UserLoginService {

    private final UserLoginPort userLoginPort;
    private final PasswordHasher passwordHasher;

    public UserLoginService(
            UserLoginPort userLoginPort,
            PasswordHasher passwordHasher) {
        this.userLoginPort = userLoginPort;
        this.passwordHasher = passwordHasher;
    }

    public LoginResult login(String loginId, String rawPassword) {
        LoginUser user = userLoginPort.findByLoginId(loginId)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return new LoginResult(user.userId(), user.loginId());
    }
    
    public Optional<LoginResult> findCurrentUser(Long userId) {
        return userLoginPort.findById(userId)
                .map(user -> new LoginResult(user.userId(), user.loginId()));
    }
}
