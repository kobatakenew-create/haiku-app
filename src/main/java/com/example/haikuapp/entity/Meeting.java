package com.example.haikuapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "meeting_name", nullable = false, length = 100)
    private String meetingName;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "theme", length = 100)
    private String theme;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Meeting() {
    }

    public Meeting(
            Long userId,
            String meetingName,
            LocalDate meetingDate,
            String theme
    ) {
        this.userId = userId;
        this.meetingName = meetingName;
        this.meetingDate = meetingDate;
        this.theme = theme;
    }

    public void update(
            String meetingName,
            LocalDate meetingDate,
            String theme
    ) {
        this.meetingName = meetingName;
        this.meetingDate = meetingDate;
        this.theme = theme;
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

    public Long getMeetingId() {
        return meetingId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public LocalDate getMeetingDate() {
        return meetingDate;
    }

    public String getTheme() {
        return theme;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}