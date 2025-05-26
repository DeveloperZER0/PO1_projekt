package com.healthtracker.model;

public enum GoalType {
    WEIGHT_LOSS("Utrata wagi", "kg", "Cel utraty wagi"),
    TARGET_WEIGHT("Waga docelowa", "kg", "Osiągnięcie określonej wagi"),
    ACTIVITY_HOURS("Aktywność tygodniowa", "h", "Godziny aktywności fizycznej tygodniowo"), // Skrócone
    DAILY_CALORIES("Dzienna kaloryczność", "kcal", "Maksymalna dzienna kaloryczność");

    private final String displayName;
    private final String unit;
    private final String description;

    GoalType(String displayName, String unit, String description) {
        this.displayName = displayName;
        this.unit = unit;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getUnit() { return unit; }
    public String getDescription() { return description; }
}
