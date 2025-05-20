package com.healthtracker.service;

import com.healthtracker.model.Meal;
import com.healthtracker.model.User;

import java.util.List;

/**
 * Interfejs dla logiki związanej z posiłkami.
 */
public interface MealService {

    /**
     * Zapisuje nowy posiłek.
     */
    void addMeal(Meal meal);

    /**
     * Zwraca wszystkie posiłki danego użytkownika.
     */
    List<Meal> getMealsByUser(User user);

    /**
     * Usuwa posiłek.
     */
    void deleteMeal(Meal meal);

    /**
     * Zwraca posiłek po ID.
     */
    Meal getById(Long id);

    // opcjonalnie:
    // List<Meal> getMealsByUserAndDate(User user, LocalDate date);
    // void updateMeal(Meal meal);
    void updateMeal(Meal meal);
}
