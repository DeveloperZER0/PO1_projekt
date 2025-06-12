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

    public static String keepOnlyDigits(String text) {
        return text != null ? text.replaceAll("[^\\d]", "") : "";
    }

    // Walidacja e-maila
    public static boolean isEmailValid(String email) {
        return matches(email, "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // Walidacja hasła (min 8 znaków, co najmniej 1 wielka litera, 1 cyfra, 1 znak specjalny)
    public static boolean isPasswordValid(String password) {
        return matches(password, "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
    }

    // Walidacja tętna
    public static boolean isHeartRateValid(Integer heartRate) {
        return heartRate != null && heartRate >= 40 && heartRate <= 220;
    }

    // Walidacja czasu trwania aktywności
    public static boolean isDurationValid(Integer duration) {
        return duration != null && duration > 0;
    }

    // Walidacja dystansu
    public static boolean isDistanceValid(Double distance) {
        return distance != null && distance > 0;
    }

    // Walidacja kalorii
    public static boolean isCaloriesValid(Integer calories) {
        return calories != null && calories > 0;
    }
}
