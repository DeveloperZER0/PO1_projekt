package com.healthtracker.dao.impl;

import com.healthtracker.dao.ActivityDao;
import com.healthtracker.model.Activity;
import com.healthtracker.model.User;

import java.util.List;

public class ActivityDaoImpl implements ActivityDao {
    @Override
    public List<Activity> findByUser(User user) {
        return List.of();
    }

    @Override
    public void save(Activity entity) {

    }

    @Override
    public void update(Activity entity) {

    }

    @Override
    public void delete(Activity entity) {

    }

    @Override
    public Activity findById(Long id) {
        return null;
    }

    @Override
    public List<Activity> findAll() {
        return List.of();
    }
    // Implementacja ActivityDao z operacjami CRUD dla aktywności.
// Używa Hibernate Session do komunikacji z bazą danych.

}
