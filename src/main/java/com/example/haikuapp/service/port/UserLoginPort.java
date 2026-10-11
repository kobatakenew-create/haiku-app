package com.example.haikuapp.service.port;

import java.util.Optional;

public interface UserLoginPort {

    Optional<LoginUser> findByLoginId(String loginId);

    Optional<LoginUser> findById(Long userId);
}
