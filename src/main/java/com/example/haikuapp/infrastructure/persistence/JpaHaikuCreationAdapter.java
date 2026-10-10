package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.entity.Haiku;
import com.example.haikuapp.repository.HaikuRepository;
import com.example.haikuapp.service.port.HaikuCreationPort;
import org.springframework.stereotype.Component;

@Component
public class JpaHaikuCreationAdapter implements HaikuCreationPort {

    private final HaikuRepository haikuRepository;

    public JpaHaikuCreationAdapter(HaikuRepository haikuRepository) {
        this.haikuRepository = haikuRepository;
    }

    @Override
    public Long save(
            Long userId,
            String text,
            String season,
            String status,
            String memo) {

        Haiku haiku = new Haiku(userId, text, season, status, memo);
        Haiku savedHaiku = haikuRepository.save(haiku);

        return savedHaiku.getHaikuId();
    }
}