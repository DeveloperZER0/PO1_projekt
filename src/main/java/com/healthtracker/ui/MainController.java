package com.healthtracker.ui;

import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Główny kontroler aplikacji – wspólna logika dla nagłówka, przycisków, wylogowania itp.
 * Może być używany jako wrapper np. w BorderPane.
 */
public class MainController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        if (SessionManager.isLoggedIn()) {
            String username = SessionManager.getCurrentUser().getUsername();
            welcomeLabel.setText("Witaj, " + username + "!");
        }
    }

    @FXML
    private void onLogoutClicked() {
        SessionManager.logout();
        SceneManager.switchScene("/com/healthtracker/views/login.fxml", "Logowanie");
    }

    // Możesz dodać więcej metod, np. do ładowania zawartości do BorderPane
}
