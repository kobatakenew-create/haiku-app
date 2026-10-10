package com.example.haikuapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_haikus")
public class MeetingHaiku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_haiku_id")
    private Long meetingHaikuId;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "haiku_id", nullable = false)
    private Long haikuId;

    @Column(name = "points")
    private Integer points;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected MeetingHaiku() {
    }

    public MeetingHaiku(Long meetingId, Long haikuId) {
        this.meetingId = meetingId;
        this.haikuId = haikuId;
    }

    public void updatePoints(Integer points) {
        this.points = points;
    }

    @PrePersist
    private void beforeInsert() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getMeetingHaikuId() {
        return meetingHaikuId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public Long getHaikuId() {
        return haikuId;
    }

    public Integer getPoints() {
        return points;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}