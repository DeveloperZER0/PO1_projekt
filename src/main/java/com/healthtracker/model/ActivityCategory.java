package com.healthtracker.model;

public enum ActivityCategory {
    CARDIO("Kardio", "🏃"),
    STRENGTH("Siłowe", "💪"),
    FLEXIBILITY("Elastyczność", "🧘"),
    WATER_SPORTS("Sporty wodne", "🏊"),
    TEAM_SPORTS("Sporty drużynowe", "⚽"),
    WINTER_SPORTS("Sporty zimowe", "⛷"),
    OUTDOOR("Na świeżym powietrzu", "🌲");

    private final String displayName;
    private final String emoji;

    ActivityCategory(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
}