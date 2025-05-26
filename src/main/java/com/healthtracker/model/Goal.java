package com.healthtracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50) // Zwiększ długość kolumny
    private GoalType goalType;

    @Column(nullable = false)
    private double targetValue;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDate createdDate;

    // Konstruktory
    public Goal() {
        this.createdDate = LocalDate.now();
    }

    public Goal(User user, GoalType goalType, double targetValue, LocalDate dueDate) {
        this();
        this.user = user;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.dueDate = dueDate;
    }

    // Gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public GoalType getGoalType() { return goalType; }
    public void setGoalType(GoalType goalType) { this.goalType = goalType; }

    public double getTargetValue() { return targetValue; }
    public void setTargetValue(double targetValue) { this.targetValue = targetValue; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}
