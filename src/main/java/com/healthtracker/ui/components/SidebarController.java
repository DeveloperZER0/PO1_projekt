package com.healthtracker.ui.components;

import com.healthtracker.ui.SceneManager;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SidebarController {

    @FXML private Button dashboardButton;
    @FXML private Button statsButton;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(e ->
                SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Dashboard")
        );
        statsButton.setOnAction(e ->
                SceneManager.switchScene("/com/healthtracker/views/statistics.fxml", "Statystyki")
        );
        profileButton.setOnAction(e ->
                SceneManager.switchScene("/com/healthtracker/views/profile.fxml", "Profil")
        );
        logoutButton.setOnAction(e -> {
            SessionManager.logout();
            SceneManager.switchScene("/com/healthtracker/views/login.fxml", "Logowanie");
        });
    }
}
