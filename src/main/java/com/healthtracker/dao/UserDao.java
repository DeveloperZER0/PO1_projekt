package com.healthtracker.dao;

import com.healthtracker.model.Role;
import com.healthtracker.model.User;

import java.util.List;

public interface UserDao extends GenericDao<User> {
    User findByUsername(String username);
    List<User> findByRole(Role role); // do widoku admina: użytkownicy danego typu
    boolean existsByUsername(String username); // przy rejestracji/logowaniu
}
