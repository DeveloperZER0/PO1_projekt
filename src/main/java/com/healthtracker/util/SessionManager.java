package com.healthtracker.util;

import com.healthtracker.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Przechowuje aktualnie zalogowanego użytkownika.
 * Używane w GUI i serwisach do sprawdzenia kontekstu.
 */
public class SessionManager {
    private static User currentUser;

    private static final Map<String, Object> sessionAttributes = new HashMap<>();

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

    public static void setAttribute(String key, Object value) {
        sessionAttributes.put(key, value);
    }

    public static Object getAttribute(String key) {
        return sessionAttributes.get(key);
    }

    public static void removeAttribute(String key) {
        sessionAttributes.remove(key);
    }

    public static void clearSession() {
        sessionAttributes.clear();
        currentUser = null;
    }
}
