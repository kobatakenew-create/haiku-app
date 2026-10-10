package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.HaikuRepository;
import com.example.haikuapp.service.port.HaikuDeletePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaHaikuDeleteAdapter implements HaikuDeletePort {

    private final HaikuRepository haikuRepository;

    public JpaHaikuDeleteAdapter(HaikuRepository haikuRepository) {
        this.haikuRepository = haikuRepository;
    }

    @Override
    @Transactional
    public boolean delete(Long userId, Long haikuId) {
        return haikuRepository
                .findByHaikuIdAndUserId(haikuId, userId)
                .map(haiku -> {
                    haikuRepository.delete(haiku);
                    return true;
                })
                .orElse(false);
    }
}