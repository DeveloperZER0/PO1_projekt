package com.healthtracker.dao;

import com.healthtracker.model.User;

public interface UserDao extends GenericDao<User> {
    User findByUsername(String username);
}
