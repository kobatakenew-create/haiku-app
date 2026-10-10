package com.example.haikuapp.repository;

import com.example.haikuapp.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RememberMeTokenRepository
        extends JpaRepository<RememberMeToken, Long> {

    Optional<RememberMeToken> findByTokenHash(String tokenHash);

    void deleteByTokenHash(String tokenHash);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
