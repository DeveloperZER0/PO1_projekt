package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.impl.ExportImportServiceImpl;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.service.impl.ActivityServiceImpl;
import com.healthtracker.service.impl.MealServiceImpl;
import com.healthtracker.service.ExportImportService;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.MealService;
import com.healthtracker.ui.components.MeasurementFormController;
import com.healthtracker.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private final MeasurementService measurementService = new MeasurementServiceImpl();
    private final ExportImportService exportImportService = new ExportImportServiceImpl(measurementService);

    // Dodaj nowe serwisy
    private final ActivityService activityService = new ActivityServiceImpl();
    private final MealService mealService = new MealServiceImpl();

    // Dodaj nowe TabPane i kontrolki
    @FXML private TabPane mainTabPane;
    @FXML private Tab measurementsTab;
    @FXML private Tab activitiesTab;
    @FXML private Tab mealsTab;

    @FXML private TableView<Activity> activitiesTable;
    @FXML private TableColumn<Activity, String> activityTypeColumn;
    @FXML private TableColumn<Activity, Integer> durationColumn;
    @FXML private TableColumn<Activity, Double> distanceColumn;
    @FXML private TableColumn<Activity, String> activityDateColumn;

    @FXML private TableView<Meal> mealsTable;
    @FXML private TableColumn<Meal, String> mealTypeColumn;
    @FXML private TableColumn<Meal, Integer> caloriesColumn;
    @FXML private TableColumn<Meal, String> descriptionColumn;
    @FXML private TableColumn<Meal, String> mealDateColumn;

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

        setupActivitiesTable();
        setupMealsTable();
        loadActivitiesData();
        loadMealsData();
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

    private void setupActivitiesTable() {
        activityTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getType().getName()));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distanceKm"));
        activityDateColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTimestamp().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
    }

    private void setupMealsTable() {
        mealTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getType().getName()));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        mealDateColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTimestamp().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
    }

    private void loadActivitiesData() {
        User currentUser = SessionManager.getCurrentUser();
        List<Activity> activities = activityService.getActivitiesByUser(currentUser);
        activitiesTable.setItems(FXCollections.observableArrayList(activities));
    }

    private void loadMealsData() {
        User currentUser = SessionManager.getCurrentUser();
        List<Meal> meals = mealService.getMealsByUser(currentUser);
        mealsTable.setItems(FXCollections.observableArrayList(meals));
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
            editItem.setOnAction(event -> {
                MeasurementRow selectedRow = row.getItem();
                if (selectedRow == null) return;

                // znajdź Measurement z bazy (po timestamp i userze)
                List<Measurement> all = measurementService.getMeasurementsByUser(SessionManager.getCurrentUser());
                Measurement toEdit = all.stream()
                        .filter(m -> m.getTimestamp().equals(selectedRow.getTimestamp()))
                        .findFirst()
                        .orElse(null);

                if (toEdit != null) {
                    try {
                        SessionManager.setAttribute("editedMeasurement", toEdit); // zapisz measurement tymczasowo
                        SceneManager.switchScene("/com/healthtracker/views/measurement_form.fxml", "Edytuj pomiar");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            MenuItem deleteItem = new MenuItem("Usuń");
            deleteItem.setOnAction(event -> {
                MeasurementRow selectedRow = row.getItem();
                if (selectedRow == null) return;

                List<Measurement> all = measurementService.getMeasurementsByUser(SessionManager.getCurrentUser());
                Measurement toDelete = all.stream()
                        .filter(m -> m.getTimestamp().equals(selectedRow.getTimestamp()))
                        .findFirst()
                        .orElse(null);

                if (toDelete != null) {
                    measurementService.deleteMeasurement(toDelete);
                    initialize(); // odśwież tabelę po usunięciu
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings
                            .when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    @FXML
    private void onAddActivity() {
        SceneManager.switchScene("/com/healthtracker/views/activity_form.fxml", "Dodaj aktywność");
    }

    @FXML
    private void onAddMeal() {
        SceneManager.switchScene("/com/healthtracker/views/meal_form.fxml", "Dodaj posiłek");
    }
}
