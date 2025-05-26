package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "activity_types")
public class ActivityType {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private boolean requiresDistance;

    @Column
    private String description;

    @Column
    private String iconName; // dla GUI

    @Enumerated(EnumType.STRING)
    private ActivityCategory category;

    // Konstruktory
    public ActivityType() {}

    public ActivityType(String name, String unit, boolean requiresDistance, ActivityCategory category) {
        this.name = name;
        this.unit = unit;
        this.requiresDistance = requiresDistance;
        this.category = category;
    }

    // gettery/settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public boolean isRequiresDistance() { return requiresDistance; }
    public void setRequiresDistance(boolean requiresDistance) { this.requiresDistance = requiresDistance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public ActivityCategory getCategory() { return category; }
    public void setCategory(ActivityCategory category) { this.category = category; }
}
