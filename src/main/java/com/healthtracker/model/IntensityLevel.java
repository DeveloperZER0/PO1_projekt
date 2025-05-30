package com.healthtracker.model;

public enum IntensityLevel {
    LOW("Niska", 1, "😌"),
    MODERATE("Umiarkowana", 2, "😊"),
    HIGH("Wysoka", 3, "😤"),
    VERY_HIGH("Bardzo wysoka", 4, "🔥");

    private final String displayName;
    private final int level;
    private final String emoji;

    IntensityLevel(String displayName, int level, String emoji) {
        this.displayName = displayName;
        this.level = level;
        this.emoji = emoji;
    }

    public String getDisplayName() { return displayName; }
    public int getLevel() { return level; }
    public String getEmoji() { return emoji; }
}