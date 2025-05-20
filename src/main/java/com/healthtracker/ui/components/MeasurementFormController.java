package com.healthtracker.ui.components;

import com.healthtracker.model.WeightMeasurement;
import com.healthtracker.model.User;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Kontroler formularza dodawania pomiaru masy ciała.
 */
public class MeasurementFormController {

    @FXML
    private TextField weightField;

    @FXML
    private Button saveButton;

    private final MeasurementService measurementService = new MeasurementServiceImpl();

    @FXML
    private void onSaveClicked() {
        try {
            double weight = Double.parseDouble(weightField.getText());
            User user = SessionManager.getCurrentUser();

            WeightMeasurement m = new WeightMeasurement();
            m.setWeight(weight);
            m.setUser(user);
            m.setTimestamp(java.time.LocalDateTime.now());

            measurementService.addMeasurement(m);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pomiar zapisany!");
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Nieprawidłowa wartość.");
            alert.showAndWait();
        }
    }
}
