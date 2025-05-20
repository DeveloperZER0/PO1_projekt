package com.healthtracker.service;

import com.healthtracker.model.Activity;
import com.healthtracker.model.User;

import java.util.List;

/**
 * Interfejs logiki aktywności fizycznych.
 */
public interface ActivityService {

    /**
     * Dodaje nową aktywność.
     */
    void addActivity(Activity activity);

    /**
     * Zwraca aktywności danego użytkownika.
     */
    List<Activity> getActivitiesByUser(User user);

    /**
     * Usuwa aktywność.
     */
    void deleteActivity(Activity activity);

    /**
     * Zwraca aktywność po ID.
     */
    Activity getById(Long id);

    // opcjonalnie:
    // List<Activity> getActivitiesBetween(User user, LocalDateTime from, LocalDateTime to);
    void updateActivity(Activity activity);
}
