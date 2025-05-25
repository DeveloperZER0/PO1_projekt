package com.healthtracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
public class Goal {
    // private Long id; @Id @GeneratedValue
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // @ManyToOne private User user;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // private String goalType;   // np. "WAGA", "KROKI"
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType goalType;
    // private double targetValue;
    private double targetValue;
    // private LocalDate dueDate;
    private LocalDate dueDate;
    // public Goal() {}
    public Goal() {}
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

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
