package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.entity.RememberMeToken;
import com.example.haikuapp.repository.RememberMeTokenRepository;
import com.example.haikuapp.service.port.RememberMeTokenData;
import com.example.haikuapp.service.port.RememberMeTokenPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class JpaRememberMeTokenAdapter implements RememberMeTokenPort {

    private final RememberMeTokenRepository tokenRepository;

    public JpaRememberMeTokenAdapter(
            RememberMeTokenRepository tokenRepository
    ) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public void save(
            Long userId,
            String tokenHash,
            LocalDateTime expiresAt
    ) {
        tokenRepository.save(
                new RememberMeToken(userId, tokenHash, expiresAt));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RememberMeTokenData> findByTokenHash(String tokenHash) {
        return tokenRepository.findByTokenHash(tokenHash)
                .map(token -> new RememberMeTokenData(
                        token.getUserId(),
                        token.getExpiresAt()
                ));
    }

    @Override
    @Transactional
    public void deleteByTokenHash(String tokenHash) {
        tokenRepository.deleteByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens(LocalDateTime now) {
        tokenRepository.deleteByExpiresAtBefore(now);
    }
}