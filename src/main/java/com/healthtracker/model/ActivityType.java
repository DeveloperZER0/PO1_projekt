package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "activity_types")
public class ActivityType {
    // private Long id; @Id @GeneratedValue
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // private String name, unit;
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String unit;

    // public ActivityType() {}
    public ActivityType() {}
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
