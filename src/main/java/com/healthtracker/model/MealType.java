package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_types")
public class MealType {
    // private Long id; @Id @GeneratedValue
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // private String name;
    @Column(nullable = false, unique = true)
    private String name;
    // public MealType() {}
    public MealType() {}
    // get/set

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
