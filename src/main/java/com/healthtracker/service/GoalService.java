package com.healthtracker.service;

import com.healthtracker.model.Goal;
import com.healthtracker.model.User;

import java.util.List;

/**
 * Interfejs do obsługi logiki związanej z celami zdrowotnymi użytkownika.
 */
public interface GoalService {

    /**
     * Dodaje nowy cel zdrowotny.
     */
    void addGoal(Goal goal);

    /**
     * Zwraca wszystkie cele danego użytkownika.
     */
    List<Goal> getGoalsByUser(User user);

    /**
     * Usuwa podany cel.
     */
    void deleteGoal(Goal goal);

    /**
     * Zwraca cel po ID.
     */
    Goal getById(Long id);

    // opcjonalnie:
    // void updateGoal(Goal goal);
    void updateGoal(Goal goal);
    // List<Goal> getActiveGoals(User user);
}
