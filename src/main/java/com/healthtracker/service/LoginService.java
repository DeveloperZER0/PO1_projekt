package com.healthtracker.service;

import com.healthtracker.dao.UserDao;
import com.healthtracker.model.User;
import com.healthtracker.util.HashUtil;
import com.healthtracker.exception.LoginFailedException;

/**
 * Klasa odpowiedzialna za logowanie użytkownika.
 * Weryfikuje dane logowania i zwraca obiekt User lub rzuca wyjątek.
 */
public class LoginService {

    private final UserDao userDao;

    public LoginService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Próbuje zalogować użytkownika. Jeśli dane są nieprawidłowe — rzuca wyjątek.
     * @param username login
     * @param rawPassword hasło wpisane przez użytkownika (jeszcze niezahashowane)
     * @return zalogowany użytkownik
     * @throws LoginFailedException jeśli login lub hasło są błędne
     */
    public User authenticate(String username, String rawPassword) {
        User user = userDao.findByUsername(username);
        if (user == null || !HashUtil.verify(rawPassword, user.getPasswordHash())) {
            throw new LoginFailedException("Nieprawidłowy login lub hasło.");
        }
        return user;
    }
}
