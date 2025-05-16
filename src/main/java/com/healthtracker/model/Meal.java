package com.healthtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meals")
public class Meal {
    // private Long id; @Id @GeneratedValue
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // @ManyToOne private User user;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // @ManyToOne private MealType type;
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private MealType type;
    // private int calories;
    @Column(nullable = false)
    private int calories;
    // private String description;
    @Column(length = 255)
    private String description;
    // private LocalDateTime timestamp;
    private LocalDateTime timestamp;
    // public Meal() {}
    public Meal() {}
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

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
