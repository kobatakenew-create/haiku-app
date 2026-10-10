package com.example.haikuapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "haikus")
public class Haiku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "haiku_id")
    private Long haikuId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "text", nullable = false, length = 20)
    private String text;

    @Column(name = "season", nullable = false, length = 10)
    private String season;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "self_evaluation", nullable = false, length = 10)
    private String selfEvaluation;

    @Column(name = "points")
    private Integer points;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Haiku() {
    }

    public Haiku(
            Long userId,
            String text,
            String season,
            String status,
            String memo
    ) {
        this.userId = userId;
        this.text = text;
        this.season = season;
        this.status = status;
        this.selfEvaluation = "無印";
        this.memo = memo;
    }

    public void update(
            String text,
            String season,
            String status,
            String selfEvaluation,
            Integer points,
            String memo
    ) {
        this.text = text;
        this.season = season;
        this.status = status;
        this.selfEvaluation = selfEvaluation;
        this.points = points;
        this.memo = memo;
    }

    @PrePersist
    private void beforeInsert() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getHaikuId() {
        return haikuId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String getSeason() {
        return season;
    }

    public String getStatus() {
        return status;
    }

    public String getSelfEvaluation() {
        return selfEvaluation;
    }

    public Integer getPoints() {
        return points;
    }

    public String getMemo() {
        return memo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}