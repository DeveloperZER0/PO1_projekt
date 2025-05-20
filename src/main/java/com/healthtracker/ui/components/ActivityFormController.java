package com.healthtracker.ui.components;

import com.healthtracker.model.Activity;
import com.healthtracker.model.ActivityType;
import com.healthtracker.model.User;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.impl.ActivityServiceImpl;
import com.healthtracker.util.SessionManager;
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
        typeCombo.getItems().addAll("Bieganie", "Rower", "Chodzenie");
    }

    @FXML
    private void onSaveClicked() {
        try {
            int duration = Integer.parseInt(durationField.getText());
            double distance = Double.parseDouble(distanceField.getText());
            String typeName = typeCombo.getValue();

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

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aktywność zapisana!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Błąd zapisu: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
