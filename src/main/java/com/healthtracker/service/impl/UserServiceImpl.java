package com.healthtracker.service.impl;

import com.healthtracker.dao.UserDao;
import com.healthtracker.dao.impl.UserDaoImpl;
import com.healthtracker.exception.RegistrationException;
import com.healthtracker.model.Role;
import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import com.healthtracker.util.HashUtil;
import com.healthtracker.util.SessionManager;
import com.healthtracker.util.ValidationUtil;

import java.util.List;

/**
 * Implementacja UserService.
 * Obsługuje rejestrację użytkownika, pobieranie użytkowników (dla admina) i pojedynczego po ID.
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public User register(User user) throws RegistrationException {
        try {
            // 1. Walidacja username
            if (!isUsernameAvailable(user.getUsername())) {
                throw new RegistrationException("Nazwa użytkownika jest już zajęta.");
            }
            // 2. Walidacja e-maila
            if (!isEmailValid(user.getEmail())) {
                throw new RegistrationException("Nieprawidłowy format adresu e-mail.");
            }
            // 3. Walidacja hasła
            if (!isPasswordValid(user.getPasswordHash())) {
                throw new RegistrationException(
                        "Hasło musi mieć minimum 8 znaków, wielką literę, cyfrę i znak specjalny."
                );
            }
            // 4. Haszowanie i zapis
            String hashed = HashUtil.hash(user.getPasswordHash());
            user.setPasswordHash(hashed);

            // 5. Ustaw datę utworzenia
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(java.time.LocalDateTime.now());
            }

            userDao.save(user);
            return user;
        } catch (Exception e) {
            if (e instanceof RegistrationException) {
                throw e; // Przepuść nasze wyjątki
            }
            throw new RegistrationException("Błąd podczas rejestracji: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userDao.existsByUsername(username);
    }

    @Override
    public boolean isEmailValid(String email) {
        return ValidationUtil.isEmailValid(email);
    }

    @Override
    public boolean isPasswordValid(String password) {
        return ValidationUtil.isPasswordValid(password);
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

    @Override
    public void deleteUser(User user) {
        // Zabezpieczenie - sprawdź czy to nie jest aktualnie zalogowany użytkownik
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && currentUser.equals(user)) {
            throw new IllegalStateException("Nie można usunąć aktualnie zalogowanego użytkownika!");
        }

        // Sprawdź czy próbuje usunąć administratora będąc administratorem
        if (SessionManager.isAdmin() && user.getRole() == Role.ADMIN && user.equals(currentUser)) {
            throw new IllegalStateException("Administrator nie może usunąć samego siebie!");
        }

        userDao.delete(user);
    }
}
