package com.healthtracker.ui;

import com.healthtracker.dao.impl.UserDaoImpl;
import com.healthtracker.exception.LoginFailedException;
import com.healthtracker.model.User;
import com.healthtracker.service.LoginService;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Kontroler odpowiedzialny za obsługę ekranu logowania.
 * Po zalogowaniu użytkownika przekierowuje do widoku głównego lub admina.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private final LoginService loginService = new LoginService(new UserDaoImpl());

    @FXML
    private void onLoginClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = loginService.authenticate(username, password);
            SessionManager.login(user);

            if (SessionManager.isAdmin()) {
                SceneManager.switchScene("/com/healthtracker/views/admin_dashboard.fxml", "Panel administratora");
            } else {
                SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");
            }

        } catch (LoginFailedException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd logowania");
            alert.setHeaderText("Nieprawidłowe dane logowania");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onRegisterLinkClicked() {
        SceneManager.switchScene("/com/healthtracker/views/registration_form.fxml", "Rejestracja");
    }

}
