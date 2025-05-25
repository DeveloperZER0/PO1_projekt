package com.healthtracker.ui.components;

import com.healthtracker.model.*;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.ui.SceneManager;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class MeasurementFormController {

    @FXML private CheckBox weightCheck, hrCheck, bpCheck;
    @FXML private TextField weightField, hrField, systolicField, diastolicField;
    @FXML private Button saveButton;
    private Measurement existingMeasurement = null;

    private final MeasurementService measurementService = new MeasurementServiceImpl();

    @FXML
    public void initialize() {
        Measurement m = (Measurement) SessionManager.getAttribute("editedMeasurement");
        if (m != null) {
            setMeasurement(m);
            SessionManager.removeAttribute("editedMeasurement");
        }
        // Możesz dodać: aktywacja pól tylko jeśli checkbox zaznaczony
        weightField.disableProperty().bind(weightCheck.selectedProperty().not());
        hrField.disableProperty().bind(hrCheck.selectedProperty().not());
        systolicField.disableProperty().bind(bpCheck.selectedProperty().not());
        diastolicField.disableProperty().bind(bpCheck.selectedProperty().not());
    }

    @FXML
    private void onSaveClicked() {
        User user = SessionManager.getCurrentUser();
        LocalDateTime now = (existingMeasurement != null) ? existingMeasurement.getTimestamp() : LocalDateTime.now();

        try {
            if (existingMeasurement != null) {
                // tryb edycji
                if (existingMeasurement instanceof WeightMeasurement wm) {
                    wm.setWeight(Double.parseDouble(weightField.getText()));
                    measurementService.updateMeasurement(wm);
                }
                if (existingMeasurement instanceof HeartRateMeasurement hr) {
                    hr.setBpm(Integer.parseInt(hrField.getText()));
                    measurementService.updateMeasurement(hr);
                }
                if (existingMeasurement instanceof BloodPressureMeasurement bp) {
                    bp.setSystolic(Integer.parseInt(systolicField.getText()));
                    bp.setDiastolic(Integer.parseInt(diastolicField.getText()));
                    measurementService.updateMeasurement(bp);
                }
            } else {
                // tryb dodawania
                if (weightCheck.isSelected()) {
                    double weight = Double.parseDouble(weightField.getText());
                    WeightMeasurement wm = new WeightMeasurement();
                    wm.setUser(user);
                    wm.setTimestamp(now);
                    wm.setWeight(weight);
                    measurementService.addMeasurement(wm);
                }

                if (hrCheck.isSelected()) {
                    int bpm = Integer.parseInt(hrField.getText());
                    HeartRateMeasurement hr = new HeartRateMeasurement();
                    hr.setUser(user);
                    hr.setTimestamp(now);
                    hr.setBpm(bpm);
                    measurementService.addMeasurement(hr);
                }

                if (bpCheck.isSelected()) {
                    int sys = Integer.parseInt(systolicField.getText());
                    int dia = Integer.parseInt(diastolicField.getText());
                    BloodPressureMeasurement bp = new BloodPressureMeasurement();
                    bp.setUser(user);
                    bp.setTimestamp(now);
                    bp.setSystolic(sys);
                    bp.setDiastolic(dia);
                    measurementService.addMeasurement(bp);
                }
            }

            SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Dashboard");


        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Wprowadź poprawne wartości liczbowe").showAndWait();
        }
    }


    @FXML
    private void onCancelClicked() {
        SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Dashboard");

    }
    public void setMeasurement(Measurement m) {
        this.existingMeasurement = m;

        if (m instanceof WeightMeasurement wm) {
            weightCheck.setSelected(true);
            weightField.setText(String.valueOf(wm.getWeight()));
        } else if (m instanceof HeartRateMeasurement hr) {
            hrCheck.setSelected(true);
            hrField.setText(String.valueOf(hr.getBpm()));
        } else if (m instanceof BloodPressureMeasurement bp) {
            bpCheck.setSelected(true);
            systolicField.setText(String.valueOf(bp.getSystolic()));
            diastolicField.setText(String.valueOf(bp.getDiastolic()));
        }

        // Ustaw timestamp jako niezmienialny
        weightField.setDisable(false);
        hrField.setDisable(false);
        systolicField.setDisable(false);
        diastolicField.setDisable(false);
    }

}
