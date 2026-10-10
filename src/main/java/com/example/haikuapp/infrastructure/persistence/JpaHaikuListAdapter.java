package com.example.haikuapp.infrastructure.persistence;

import com.example.haikuapp.repository.HaikuRepository;
import com.example.haikuapp.service.port.HaikuListPort;
import com.example.haikuapp.service.port.HaikuSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaHaikuListAdapter implements HaikuListPort {

    private final HaikuRepository haikuRepository;

    public JpaHaikuListAdapter(HaikuRepository haikuRepository) {
        this.haikuRepository = haikuRepository;
    }

    @Override
    public List<HaikuSummary> findByUserId(Long userId) {
        return haikuRepository
                .findByUserIdOrderByCreatedAtDescHaikuIdDesc(userId)
                .stream()
                .map(haiku -> new HaikuSummary(
                        haiku.getHaikuId(),
                        haiku.getText(),
                        haiku.getSeason(),
                        haiku.getStatus(),
                        haiku.getSelfEvaluation(),
                        haiku.getPoints(),
                        haiku.getMemo(),
                        haiku.getCreatedAt()
                ))
                .toList();
    }
}