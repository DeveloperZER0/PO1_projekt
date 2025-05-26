package com.healthtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    private ActivityType type;

    @Column(nullable = false)
    private int durationMinutes;

    @Column
    private Double distanceKm; // Nullable dla aktywności bez dystansu

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private Integer caloriesBurned; // Szacowane kalorie

    @Column
    private Integer heartRateAvg; // Średnie tętno

    @Column
    private Integer heartRateMax; // Maksymalne tętno

    @Column(length = 500)
    private String notes; // Notatki użytkownika

    @Enumerated(EnumType.STRING)
    private IntensityLevel intensity;

    @Column
    private Double avgSpeed; // km/h dla aktywności z dystansem

    // Konstruktory
    public Activity() {}

    public Activity(User user, ActivityType type, int durationMinutes) {
        this.user = user;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.timestamp = LocalDateTime.now();
    }

    // Metody biznesowe
    public double calculateAvgSpeed() {
        if (distanceKm != null && durationMinutes > 0) {
            return (distanceKm * 60.0) / durationMinutes;
        }
        return 0.0;
    }

    public int estimateCaloriesBurn(double userWeightKg) {
        // Prosta kalkulacja na podstawie typu aktywności i czasu
        double metValue = getMetValue();
        return (int) ((metValue * userWeightKg * durationMinutes) / 60.0);
    }

    private double getMetValue() {
        // MET values dla różnych aktywności
        return switch (type.getName().toLowerCase()) {
            case "bieganie" -> 8.0;
            case "rower" -> 6.0;
            case "chodzenie" -> 3.5;
            case "pływanie" -> 7.0;
            case "siłownia" -> 5.0;
            case "joga" -> 2.5;
            case "aerobik" -> 6.5;
            default -> 4.0;
        };
    }

    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(type.getName()).append(" - ").append(durationMinutes).append(" min");
        
        if (distanceKm != null && distanceKm > 0) {
            summary.append(", ").append(String.format("%.2f", distanceKm)).append(" km");
            summary.append(" (").append(String.format("%.1f", calculateAvgSpeed())).append(" km/h)");
        }
        
        if (caloriesBurned != null) {
            summary.append(", ").append(caloriesBurned).append(" kcal");
        }
        
        return summary.toString();
    }

    // Wszystkie gettery/settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { 
        this.distanceKm = distanceKm;
        if (distanceKm != null && durationMinutes > 0) {
            this.avgSpeed = calculateAvgSpeed();
        }
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public Integer getHeartRateAvg() { return heartRateAvg; }
    public void setHeartRateAvg(Integer heartRateAvg) { this.heartRateAvg = heartRateAvg; }

    public Integer getHeartRateMax() { return heartRateMax; }
    public void setHeartRateMax(Integer heartRateMax) { this.heartRateMax = heartRateMax; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public IntensityLevel getIntensity() { return intensity; }
    public void setIntensity(IntensityLevel intensity) { this.intensity = intensity; }

    public Double getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(Double avgSpeed) { this.avgSpeed = avgSpeed; }
}
