package com.healthtracker.service.impl;

import com.healthtracker.dao.UserDao;
import com.healthtracker.dao.impl.UserDaoImpl;
import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import com.healthtracker.util.HashUtil;
import com.healthtracker.util.ValidationUtil;

import java.util.List;

/**
 * Implementacja UserService.
 * Obsługuje rejestrację użytkownika, pobieranie użytkowników (dla admina) i pojedynczego po ID.
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public User register(User user) {
        // 1. Walidacja username
        if (!isUsernameAvailable(user.getUsername())) {
            throw new IllegalArgumentException("Nazwa użytkownika jest zajęta.");
        }
        // 2. Walidacja e-maila
        if (!isEmailValid(user.getEmail())) {
            throw new IllegalArgumentException("Nieprawidłowy format e-maila.");
        }
        // 3. Walidacja hasła
        if (!isPasswordValid(user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Hasło musi mieć min. 8 znaków, wielką literę, cyfrę i znak specjalny."
            );
        }
        // 4. Haszowanie i zapis
        String hashed = HashUtil.hash(user.getPasswordHash());
        user.setPasswordHash(hashed);
        userDao.save(user);
        return user;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userDao.existsByUsername(username);
    }

    @Override
    public boolean isEmailValid(String email) {
        // prosty regex e-mail
        return ValidationUtil.matches(email,
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    @Override
    public boolean isPasswordValid(String password) {
        // min 8 znaków, co najmniej 1 wielka litera, 1 cyfra, 1 znak specjalny
        return ValidationUtil.matches(password,
                "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User getById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public void updateUser(User user) {
        userDao.update(user);
    }
}
