package com.healthtracker.ui;

import com.healthtracker.model.Role;
import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import com.healthtracker.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegistrationController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void onRegisterClicked() {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String pwd      = passwordField.getText();
        String pwd2     = confirmPasswordField.getText();

        // 1. Sprawdź powtórzenie hasła
        if (!pwd.equals(pwd2)) {
            showError("Hasła nie są identyczne.");
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(pwd);
            user.setRole(Role.USER);

            userService.register(user);
            // Po udanej rejestracji wróć do logowania:
            SceneManager.switchScene("/com/healthtracker/views/login.fxml", "Logowanie");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rejestracja zakończona sukcesem!");
            alert.showAndWait();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Błąd podczas rejestracji: " + ex.getMessage());
        }
    }

    @FXML
    private void onLoginLinkClicked() {
        SceneManager.switchScene("/com/healthtracker/views/login.fxml", "Logowanie");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd rejestracji");
        alert.setHeaderText("Nieprawidłowe dane");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
