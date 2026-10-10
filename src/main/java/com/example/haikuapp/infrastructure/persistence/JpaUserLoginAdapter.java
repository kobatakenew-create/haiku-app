package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.UserRepository;
import com.example.haikuapp.service.port.LoginUser;
import com.example.haikuapp.service.port.UserLoginPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaUserLoginAdapter implements UserLoginPort {

    private final UserRepository userRepository;

    public JpaUserLoginAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<LoginUser> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(user -> new LoginUser(
                        user.getUserId(),
                        user.getLoginId(),
                        user.getPasswordHash()));
    }
}