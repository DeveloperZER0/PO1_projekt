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
import javafx.stage.Stage;

import java.time.LocalDateTime;

/**
 * Kontroler formularza dodawania pomiaru masy ciała.
 */
public class MeasurementFormController {

    @FXML
    private TextField weightField;

    @FXML
    private Button saveButton;

    private final MeasurementService measurementService = new MeasurementServiceImpl();

    private WeightMeasurement editing;

    public void setMeasurement(WeightMeasurement wm) {
        this.editing = wm;
        weightField.setText(String.valueOf(wm.getWeight()));
    }

    @FXML
    private void onSaveClicked() {
        try {
            double weight = Double.parseDouble(weightField.getText());
            if (editing != null) {
                editing.setWeight(weight);
                editing.setTimestamp(LocalDateTime.now());
                editing.setUser(SessionManager.getCurrentUser());
                new MeasurementServiceImpl().addMeasurement(editing);
            } else {
                WeightMeasurement wm = new WeightMeasurement();
                wm.setUser(SessionManager.getCurrentUser());
                wm.setWeight(weight);
                wm.setTimestamp(LocalDateTime.now());
                new MeasurementServiceImpl().addMeasurement(wm);
            }

            ((Stage) saveButton.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Nieprawidłowa waga").showAndWait();
        }
    }
}
