package com.healthtracker.util;

import com.healthtracker.model.User;

/**
 * Przechowuje aktualnie zalogowanego użytkownika.
 * Używane w GUI i serwisach do sprawdzenia kontekstu.
 */
public class SessionManager {
    private static User currentUser;

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole().name().equalsIgnoreCase("ADMIN");
    }
}
