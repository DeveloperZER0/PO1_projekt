package com.healthtracker.dao;

import com.healthtracker.model.Meal;
import com.healthtracker.model.User;

import java.util.List;

public interface MealDao extends GenericDao<Meal>{
    List<Meal> findByUser(User user);
    /*
    * List<Meal> findByUserAndDate(User user, LocalDate date);
    * List<Meal> findByUserBetweenDates(User user, LocalDate start, LocalDate end);
    */
}
