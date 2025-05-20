package com.healthtracker.service.impl;

import com.healthtracker.dao.MealDao;
import com.healthtracker.dao.impl.MealDaoImpl;
import com.healthtracker.model.Meal;
import com.healthtracker.model.User;
import com.healthtracker.service.MealService;

import java.util.List;

public class MealServiceImpl implements MealService {
    private final MealDao mealDao = new MealDaoImpl();

    @Override
    public void addMeal(Meal meal) {
        mealDao.save(meal);
    }

    @Override
    public List<Meal> getMealsByUser(User user) {
        return mealDao.findByUser(user);
    }

    @Override
    public void deleteMeal(Meal meal) {
        mealDao.delete(meal);
    }

    @Override
    public Meal getById(Long id) {
        return mealDao.findById(id);
    }

    @Override
    public void updateMeal(Meal meal) {
        mealDao.update(meal);
    }
}
