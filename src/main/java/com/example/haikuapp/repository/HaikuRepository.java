package com.example.haikuapp.repository;

import com.example.haikuapp.entity.Haiku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HaikuRepository extends JpaRepository<Haiku, Long> {

    List<Haiku> findByUserIdOrderByCreatedAtDescHaikuIdDesc(Long userId);

    Optional<Haiku> findByHaikuIdAndUserId(Long haikuId, Long userId);
}