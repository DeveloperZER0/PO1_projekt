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
import java.util.List;

public class MeasurementFormController {

    @FXML private CheckBox weightCheck, hrCheck, bpCheck;
    @FXML private TextField weightField, hrField, systolicField, diastolicField;
    @FXML private Button saveButton;
    private List<Measurement> existingMeasurements; // Zmień z pojedynczego na listę

    private final MeasurementService measurementService = new MeasurementServiceImpl();

    @FXML
    public void initialize() {
        // Sprawdź czy edytujemy pomiary
        @SuppressWarnings("unchecked")
        List<Measurement> measurements = (List<Measurement>) SessionManager.getAttribute("editedMeasurements");
        if (measurements != null) {
            setMeasurements(measurements);
            SessionManager.removeAttribute("editedMeasurements");
        }
        
        // Możesz dodać: aktywacja pól tylko jeśli checkbox zaznaczony
        weightField.disableProperty().bind(weightCheck.selectedProperty().not());
        hrField.disableProperty().bind(hrCheck.selectedProperty().not());
        systolicField.disableProperty().bind(bpCheck.selectedProperty().not());
        diastolicField.disableProperty().bind(bpCheck.selectedProperty().not());
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.existingMeasurements = measurements;
        
        // Wypełnij pola danymi z pomiarów
        for (Measurement m : measurements) {
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
        }
    }

    // Zachowaj dla kompatybilności wstecznej
    public void setMeasurement(Measurement m) {
        this.existingMeasurements = List.of(m);
        setMeasurements(this.existingMeasurements);
    }

    @FXML
    private void onSaveClicked() {
        User user = SessionManager.getCurrentUser();
        LocalDateTime now = (existingMeasurements != null && !existingMeasurements.isEmpty()) 
            ? existingMeasurements.get(0).getTimestamp() 
            : LocalDateTime.now();

        try {
            boolean isEditing = existingMeasurements != null && !existingMeasurements.isEmpty();
            
            if (isEditing) {
                // Tryb edycji - znajdź istniejące pomiary do aktualizacji
                WeightMeasurement existingWeight = findMeasurementByType(WeightMeasurement.class);
                HeartRateMeasurement existingHR = findMeasurementByType(HeartRateMeasurement.class);
                BloodPressureMeasurement existingBP = findMeasurementByType(BloodPressureMeasurement.class);
                
                // Waga
                if (weightCheck.isSelected()) {
                    double weight = Double.parseDouble(weightField.getText());
                    if (existingWeight != null) {
                        existingWeight.setWeight(weight);
                        measurementService.updateMeasurement(existingWeight);
                    } else {
                        // Dodaj nowy pomiar wagi do istniejącego timestamp
                        WeightMeasurement newWeight = new WeightMeasurement();
                        newWeight.setUser(user);
                        newWeight.setTimestamp(now);
                        newWeight.setWeight(weight);
                        measurementService.addMeasurement(newWeight);
                    }
                } else if (existingWeight != null) {
                    // Usuń pomiar wagi jeśli odznaczono checkbox
                    measurementService.deleteMeasurement(existingWeight);
                }
                
                // Tętno
                if (hrCheck.isSelected()) {
                    int hr = Integer.parseInt(hrField.getText());
                    if (existingHR != null) {
                        existingHR.setBpm(hr);
                        measurementService.updateMeasurement(existingHR);
                    } else {
                        HeartRateMeasurement newHR = new HeartRateMeasurement();
                        newHR.setUser(user);
                        newHR.setTimestamp(now);
                        newHR.setBpm(hr);
                        measurementService.addMeasurement(newHR);
                    }
                } else if (existingHR != null) {
                    measurementService.deleteMeasurement(existingHR);
                }
                
                // Ciśnienie
                if (bpCheck.isSelected()) {
                    int systolic = Integer.parseInt(systolicField.getText());
                    int diastolic = Integer.parseInt(diastolicField.getText());
                    if (existingBP != null) {
                        existingBP.setSystolic(systolic);
                        existingBP.setDiastolic(diastolic);
                        measurementService.updateMeasurement(existingBP);
                    } else {
                        BloodPressureMeasurement newBP = new BloodPressureMeasurement();
                        newBP.setUser(user);
                        newBP.setTimestamp(now);
                        newBP.setSystolic(systolic);
                        newBP.setDiastolic(diastolic);
                        measurementService.addMeasurement(newBP);
                    }
                } else if (existingBP != null) {
                    measurementService.deleteMeasurement(existingBP);
                }
                
                new Alert(Alert.AlertType.INFORMATION, "Pomiary zostały zaktualizowane!").showAndWait();
            } else {
                // Tryb dodawania - jak wcześniej
                if (weightCheck.isSelected()) {
                    double weight = Double.parseDouble(weightField.getText());
                    WeightMeasurement weightMeasurement = new WeightMeasurement();
                    weightMeasurement.setUser(user);
                    weightMeasurement.setTimestamp(now);
                    weightMeasurement.setWeight(weight);
                    measurementService.addMeasurement(weightMeasurement);
                }

                if (hrCheck.isSelected()) {
                    int hr = Integer.parseInt(hrField.getText());
                    HeartRateMeasurement hrMeasurement = new HeartRateMeasurement();
                    hrMeasurement.setUser(user);
                    hrMeasurement.setTimestamp(now);
                    hrMeasurement.setBpm(hr);
                    measurementService.addMeasurement(hrMeasurement);
                }

                if (bpCheck.isSelected()) {
                    int systolic = Integer.parseInt(systolicField.getText());
                    int diastolic = Integer.parseInt(diastolicField.getText());
                    BloodPressureMeasurement bpMeasurement = new BloodPressureMeasurement();
                    bpMeasurement.setUser(user);
                    bpMeasurement.setTimestamp(now);
                    bpMeasurement.setSystolic(systolic);
                    bpMeasurement.setDiastolic(diastolic);
                    measurementService.addMeasurement(bpMeasurement);
                }
                
                new Alert(Alert.AlertType.INFORMATION, "Pomiary zostały zapisane!").showAndWait();
            }

            SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Wprowadź poprawne wartości liczbowe").showAndWait();
        }
    }

    @FXML
    private void onCancelClicked() {
        SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");
    }

    @SuppressWarnings("unchecked")
    private <T extends Measurement> T findMeasurementByType(Class<T> type) {
        if (existingMeasurements == null) return null;
        
        return existingMeasurements.stream()
            .filter(type::isInstance)
            .map(m -> (T) m)
            .findFirst()
            .orElse(null);
    }

}
