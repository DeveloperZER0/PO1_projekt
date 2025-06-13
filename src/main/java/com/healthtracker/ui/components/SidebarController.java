package com.healthtracker.ui.components;

import com.healthtracker.ui.SceneManager;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class SidebarController {

    @FXML private Button dashboardButton;
    @FXML private Button statsButton;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;
    @FXML private ImageView logoImageView;

    @FXML
    public void initialize() {
        // Załaduj logo
        try {
            logoImageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/healthtracker/img/logo_biale.png")));
            logoImageView.setFitWidth(28);
            logoImageView.setFitHeight(28);
            logoImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Nie można załadować logo: " + e.getMessage());
        }
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
