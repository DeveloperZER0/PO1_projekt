package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.impl.ExportImportServiceImpl;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.service.ExportImportService;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserDashboardController {

    @FXML private TableView<MeasurementRow> measurementTable;
    @FXML private TableColumn<MeasurementRow, String> timestampColumn;
    @FXML private TableColumn<MeasurementRow, String> bpColumn;
    @FXML private TableColumn<MeasurementRow, String> hrColumn;
    @FXML private TableColumn<MeasurementRow, String> weightColumn;
    @FXML private TableColumn<MeasurementRow, String> summaryColumn;
    @FXML private Button addMeasurementButton;
    private final MeasurementService measurementService = new MeasurementServiceImpl();
    private final ExportImportService exportImportService = new ExportImportServiceImpl(measurementService);

    public void initialize() {
        timestampColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        bpColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBloodPressure()));
        hrColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getHeartRate()));
        weightColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWeight()));
        summaryColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSummary()));

        loadData();
    }

    private void loadData() {
        List<Measurement> all = measurementService.getMeasurementsByUser(SessionManager.getCurrentUser());
        Map<String, MeasurementRow> grouped = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Measurement m : all) {
            String key = m.getTimestamp().format(fmt);
            grouped.putIfAbsent(key, new MeasurementRow(m.getTimestamp()));
            MeasurementRow row = grouped.get(key);

            switch (m) {
                case BloodPressureMeasurement bpm -> row.setBloodPressure(bpm.getSystolic() + "/" + bpm.getDiastolic() + " mmHg");
                case HeartRateMeasurement hr -> row.setHeartRate(hr.getBpm() + " BPM");
                case WeightMeasurement wm -> row.setWeight(wm.getWeight() + " kg");
                default -> {
                }
            }
        }

        measurementTable.setItems(FXCollections.observableArrayList(grouped.values()));
    }

    @FXML
    private void onAddMeasurement() {
        SceneManager.switchScene("/com/healthtracker/views/measurement_form.fxml", "Nowy pomiar");
    }

    @FXML
    private void onExportClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("pomiary.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                exportImportService.exportMeasurementsToCsv(SessionManager.getCurrentUser(), file);
                new Alert(Alert.AlertType.INFORMATION, "Dane zostały wyeksportowane.").showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Błąd eksportu: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    private void onImportClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik CSV do importu");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                exportImportService.importMeasurementsFromCsv(SessionManager.getCurrentUser(), file);
                new Alert(Alert.AlertType.INFORMATION, "Import zakończony!").showAndWait();
                loadData();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Błąd importu: " + e.getMessage()).showAndWait();
            }
        }
    }
}
