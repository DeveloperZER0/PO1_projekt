package com.healthtracker.service;

import com.healthtracker.model.User;
import java.util.List;

public interface UserService {
    User register(User user);
    List<User> getAllUsers();           // dla admina
    User getById(Long id);
    // inne metody związane z profilem
}
