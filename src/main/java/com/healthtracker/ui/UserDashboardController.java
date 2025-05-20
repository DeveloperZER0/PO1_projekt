package com.healthtracker.ui;

import com.healthtracker.model.Measurement;
import com.healthtracker.model.User;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

/**
 * Kontroler panelu użytkownika.
 * Wyświetla listę pomiarów oraz umożliwia przejście do dodawania nowego.
 */
public class UserDashboardController {

    @FXML
    private TableView<Measurement> measurementTable;

    @FXML
    private Button addMeasurementButton;

    private final MeasurementService measurementService = new MeasurementServiceImpl();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            measurementTable.setItems(FXCollections.observableArrayList(
                    measurementService.getMeasurementsByUser(currentUser)
            ));
        }
    }

    @FXML
    public void onAddMeasurement(ActionEvent actionEvent) {
        SceneManager.switchScene("/com/healthtracker/views/measurement_form.fxml", "Dodaj pomiar");

    }
}
