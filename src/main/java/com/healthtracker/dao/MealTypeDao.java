package com.healthtracker.dao;
import com.healthtracker.model.MealType;
import java.util.List;
import com.healthtracker.dao.GenericDao;

public interface MealTypeDao extends GenericDao<MealType> {
    /**
     * Finds a MealType by its name.
     *
     * @param name the name of the MealType
     * @return the MealType with the specified name, or null if not found
     */
    MealType findByName(String name);
    
    /**
     * Finds all MealTypes that are considered main meals.
     *
     * @return a list of main meal types
     */
    List<MealType> findMainMeals();
}