package com.healthtracker.util;

public class ValidationUtil {

    public static boolean isPositive(double value) {
        return value > 0;
    }

    public static boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean matches(String text, String regex) {
        return text != null && text.matches(regex);
    }

    // Dodaj inne metody, np. walidację e-maila, hasła itd.
}
