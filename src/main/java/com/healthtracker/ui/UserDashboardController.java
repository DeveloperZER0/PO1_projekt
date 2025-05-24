package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.impl.ExportImportServiceImpl;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.service.ExportImportService;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.ui.components.MeasurementFormController;
import com.healthtracker.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        setupContextMenu();
        loadData();
        measurementTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                MeasurementRow selectedRow = measurementTable.getSelectionModel().getSelectedItem();
                if (selectedRow == null) return;

                List<Measurement> all = measurementService.getMeasurementsByUser(SessionManager.getCurrentUser());

                Measurement target = all.stream()
                        .filter(m -> m.getTimestamp().equals(selectedRow.getTimestamp()))
                        .findFirst().orElse(null);

                if (target != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/healthtracker/views/measurement_detail.fxml"));
                        Parent root = loader.load();

                        MeasurementDetailController controller = loader.getController();
                        controller.setMeasurement(target, all);

                        Stage stage = new Stage();
                        stage.setTitle("Szczegóły pomiaru");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    private void setupContextMenu() {
        measurementTable.setRowFactory(tv -> {
            TableRow<MeasurementRow> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edytuj");
            editItem.setOnAction(e -> {
                MeasurementRow selected = row.getItem();
                showEditForm(selected);
            });

            contextMenu.getItems().add(editItem);
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    private void showEditForm(MeasurementRow row) {
        // Można bazować na timestamp i typie pomiaru, by pobrać Measurement z bazy
        // Dla uproszczenia zakładamy, że chcesz edytować wagę:
        List<Measurement> all = measurementService.getMeasurementsByUser(SessionManager.getCurrentUser());
        for (Measurement m : all) {
            if (m instanceof WeightMeasurement wm &&
                    wm.getTimestamp().equals(row.getTimestamp())) {

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/healthtracker/views/measurement_form.fxml"));
                    Parent form = loader.load();
                    MeasurementFormController controller = loader.getController();
                    controller.setMeasurement(wm);

                    Stage stage = new Stage();
                    stage.setTitle("Edytuj pomiar");
                    stage.setScene(new Scene(form));
                    stage.showAndWait();

                    initialize(); // Odświeżenie tabeli
                    return;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
