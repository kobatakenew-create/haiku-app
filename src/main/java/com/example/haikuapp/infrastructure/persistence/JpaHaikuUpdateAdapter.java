package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.HaikuRepository;
import com.example.haikuapp.service.port.HaikuUpdatePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaHaikuUpdateAdapter implements HaikuUpdatePort {

    private final HaikuRepository haikuRepository;

    public JpaHaikuUpdateAdapter(HaikuRepository haikuRepository) {
        this.haikuRepository = haikuRepository;
    }

    @Override
    @Transactional
    public boolean update(
            Long userId,
            Long haikuId,
            String text,
            String season,
            String status,
            String selfEvaluation,
            Integer points,
            String memo
    ) {
        return haikuRepository
                .findByHaikuIdAndUserId(haikuId, userId)
                .map(haiku -> {
                    haiku.update(
                            text,
                            season,
                            status,
                            selfEvaluation,
                            points,
                            memo
                    );
                    return true;
                })
                .orElse(false);
    }
}