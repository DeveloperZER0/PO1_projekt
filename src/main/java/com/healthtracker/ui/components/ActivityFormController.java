package com.healthtracker.ui.components;

import com.healthtracker.model.Activity;
import com.healthtracker.model.ActivityType;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.impl.ActivityServiceImpl;
import com.healthtracker.util.SessionManager;
import com.healthtracker.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

/**
 * Kontroler formularza dodawania aktywności fizycznej.
 */
public class ActivityFormController {

    @FXML
    private TextField durationField;

    @FXML
    private TextField distanceField;

    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private Button saveButton;

    private final ActivityService activityService = new ActivityServiceImpl();

    @FXML
    private void initialize() {
        typeCombo.getItems().addAll("Bieganie", "Rower", "Chodzenie", "Pływanie", "Siłownia", "Joga");
    }

    @FXML
    private void onSaveClicked() {
        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            double distance = Double.parseDouble(distanceField.getText().trim());
            String typeName = typeCombo.getValue();

            if (typeName == null || typeName.isEmpty()) {
                showError("Wybierz typ aktywności");
                return;
            }

            if (duration <= 0) {
                showError("Czas trwania musi być większy od 0");
                return;
            }

            if (distance < 0) {
                showError("Dystans nie może być ujemny");
                return;
            }

            ActivityType type = new ActivityType();
            type.setName(typeName);
            type.setUnit("km"); // domyślnie

            Activity activity = new Activity();
            activity.setUser(SessionManager.getCurrentUser());
            activity.setDurationMinutes(duration);
            activity.setDistanceKm(distance);
            activity.setType(type);
            activity.setTimestamp(LocalDateTime.now());

            activityService.addActivity(activity);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aktywność została zapisana!");
            alert.showAndWait();

            SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");

        } catch (NumberFormatException e) {
            showError("Wprowadź poprawne wartości liczbowe");
        } catch (Exception e) {
            showError("Błąd zapisu: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelClicked() {
        SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
