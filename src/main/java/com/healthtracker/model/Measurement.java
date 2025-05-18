package com.healthtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "measurements")
public abstract class Measurement {
    // protected Long id; @Id @GeneratedValue
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    // protected LocalDateTime timestamp;
    @Column(nullable = false)
    protected LocalDateTime timestamp;

    // @ManyToOne protected User user;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    // public Measurement() {}     ← pusty
    // public abstract String getSummary();  ← do wyświetlania w tabeli
    public Measurement() {}
    public abstract String getSummary();

    // gettery / settery

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
