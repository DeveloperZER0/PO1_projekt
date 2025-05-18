package com.healthtracker.service.impl;

import com.healthtracker.dao.UserDao;
import com.healthtracker.model.User;
import com.healthtracker.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public User register(User user) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public User getById(Long id) {
        return null;
    }
    // private UserDao userDao;
    // implementuj register, getAllUsers, getById
    // podczas rejestracji haszuj hasło (HashUtil)
}
