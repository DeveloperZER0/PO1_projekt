package com.healthtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {
    // private Long id; @Id @GeneratedValue
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne private User user;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // @ManyToOne private ActivityType type;
    @ManyToOne
    private ActivityType type;

    // private int durationMinutes;
    private int durationMinutes;

    // private double distanceKm;
    private double distanceKm;

    // private LocalDateTime timestamp;
    private LocalDateTime timestamp;

    // public Activity() {}
    public Activity() {}

    // get/set

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
