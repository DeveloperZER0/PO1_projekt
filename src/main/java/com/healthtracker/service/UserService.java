package com.healthtracker.service;

import com.healthtracker.model.User;
import java.util.List;

/**
 * Serwis odpowiedzialny za operacje na użytkownikach.
 * Używany do rejestracji, pobierania danych oraz listowania użytkowników.
 */
public interface UserService {

    User register(User user) throws IllegalArgumentException;
    boolean isUsernameAvailable(String username);
    boolean isEmailValid(String email);
    boolean isPasswordValid(String password);
    List<User> getAllUsers();
    User getById(Long id);
    void updateUser(User user);
}
