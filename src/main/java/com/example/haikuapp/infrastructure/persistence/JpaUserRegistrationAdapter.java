package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.entity.User;
import com.example.haikuapp.repository.UserRepository;
import com.example.haikuapp.service.port.UserRegistrationPort;
import org.springframework.stereotype.Component;

@Component
public class JpaUserRegistrationAdapter implements UserRegistrationPort {

    private final UserRepository userRepository;

    public JpaUserRegistrationAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Override
    public Long save(String loginId, String passwordHash) {
        User user = new User(loginId, passwordHash);
        User savedUser = userRepository.save(user);
        return savedUser.getUserId();
    }
}