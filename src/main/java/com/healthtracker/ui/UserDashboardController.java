package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.*;
import com.healthtracker.service.impl.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final GoalService goalService = new GoalServiceImpl();

    // Dodaj nowe TabPane i kontrolki
    @FXML private TabPane mainTabPane;
    @FXML private Tab measurementsTab;
    @FXML private Tab activitiesTab;
    @FXML private Tab mealsTab;
    @FXML private Tab goalsTab;

    // Activity table columns - POPRAWKA: Zmień wszystkie na String
    @FXML private TableView<Activity> activitiesTable;
    @FXML private TableColumn<Activity, String> activityTypeColumn;
    @FXML private TableColumn<Activity, String> durationColumn; // ZMIENIONE z Integer na String
    @FXML private TableColumn<Activity, String> distanceColumn;
    @FXML private TableColumn<Activity, String> activityDateColumn;
    @FXML private TableColumn<Activity, String> activityCaloriesColumn;
    @FXML private TableColumn<Activity, String> intensityColumn;
    @FXML private TableColumn<Activity, String> speedColumn;

    // Meal table columns - POPRAWKA: Zmień mealCaloriesColumn na String  
    @FXML private TableView<Meal> mealsTable;
    @FXML private TableColumn<Meal, String> mealTypeColumn;
    @FXML private TableColumn<Meal, String> mealCaloriesColumn; // ZMIENIONE z Integer na String
    @FXML private TableColumn<Meal, String> descriptionColumn;
    @FXML private TableColumn<Meal, String> mealDateColumn;

    // Goals table columns
    @FXML private TableView<Goal> goalsTable;
    @FXML private TableColumn<Goal, String> goalTypeColumn;
    @FXML private TableColumn<Goal, String> goalTargetColumn;
    @FXML private TableColumn<Goal, String> goalCurrentColumn;
    @FXML private TableColumn<Goal, String> goalProgressColumn;
    @FXML private TableColumn<Goal, String> goalDueColumn;
    @FXML private TableColumn<Goal, String> goalStatusColumn;

    @FXML
    public void initialize() {
        // Pomiary - lepsze formatowanie kolumn
        timestampColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\nHH:mm"))));
        
        bpColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBloodPressure()));
        // USUŃ: bpColumn.setStyle("-fx-alignment: CENTER;");
        
        hrColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getHeartRate()));
        // USUŃ: hrColumn.setStyle("-fx-alignment: CENTER;");
        
        weightColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWeight()));
        // USUŃ: weightColumn.setStyle("-fx-alignment: CENTER;");
        
        summaryColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSummary()));
        
        // Ustaw preferowane szerokości w proporcjach
        timestampColumn.prefWidthProperty().bind(measurementTable.widthProperty().multiply(0.2));
        bpColumn.prefWidthProperty().bind(measurementTable.widthProperty().multiply(0.15));
        hrColumn.prefWidthProperty().bind(measurementTable.widthProperty().multiply(0.1));
        weightColumn.prefWidthProperty().bind(measurementTable.widthProperty().multiply(0.1));
        summaryColumn.prefWidthProperty().bind(measurementTable.widthProperty().multiply(0.45));
        
        // Ustaw welcome label
//        if (SessionManager.isLoggedIn()) {
//            userWelcomeLabel.setText("Witaj, " + SessionManager.getCurrentUser().getUsername() + "!");
//        }
        
        setupContextMenu();
        setupActivitiesContextMenu();
        setupMealsContextMenu();
        setupGoalsContextMenu();
        loadData();

        setupActivitiesTable();
        setupMealsTable();
        setupGoalsTable();
        loadActivitiesData();
        loadMealsData();
        loadGoalsData();
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
        // Typ aktywności z emoji
        activityTypeColumn.setCellValueFactory(cellData -> {
            Activity activity = cellData.getValue();
            ActivityType type = activity.getType();
            return new SimpleStringProperty(
                type.getCategory().getEmoji() + " " + type.getName()
            );
        });
        
        durationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDurationMinutes() + " min"));
        
        distanceColumn.setCellValueFactory(cellData -> {
            Activity activity = cellData.getValue();
            if (activity.getDistanceKm() != null && activity.getDistanceKm() > 0) {
                if ("m".equals(activity.getType().getUnit())) {
                    return new SimpleStringProperty(String.format("%.0f m", activity.getDistanceKm() * 1000));
                } else {
                    return new SimpleStringProperty(String.format("%.2f km", activity.getDistanceKm()));
                }
            }
            return new SimpleStringProperty("-");
        });
        
        activityCaloriesColumn.setCellValueFactory(cellData -> {
            Integer calories = cellData.getValue().getCaloriesBurned();
            return new SimpleStringProperty(calories != null ? calories + " kcal" : "-");
        });
        
        intensityColumn.setCellValueFactory(cellData -> {
            IntensityLevel intensity = cellData.getValue().getIntensity();
            return new SimpleStringProperty(intensity != null ? 
                intensity.getEmoji() + " " + intensity.getDisplayName() : "-");
        });
        
        speedColumn.setCellValueFactory(cellData -> {
            Activity activity = cellData.getValue();
            if (activity.getDistanceKm() != null && activity.getDistanceKm() > 0) {
                double speed = activity.calculateAvgSpeed();
                return new SimpleStringProperty(String.format("%.1f km/h", speed));
            }
            return new SimpleStringProperty("-");
        });
        
        activityDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTimestamp()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy\nHH:mm"))));
        
        // Ustaw proporcjonalne szerokości dla tabeli aktywności
        activityTypeColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.18));
        durationColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.12));
        distanceColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.12));
        activityCaloriesColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.1));
        intensityColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.15));
        speedColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.13));
        activityDateColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.2));
    }

    private void setupMealsTable() {
        mealTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getType().getName()));
        
        mealCaloriesColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCalories() + " kcal"));
        
        descriptionColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDescription()));
        
        mealDateColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTimestamp()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy\nHH:mm"))));
        
        // Ustaw proporcjonalne szerokości dla tabeli posiłków
        mealTypeColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.18));
        mealCaloriesColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.12));
        descriptionColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.48));
        mealDateColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.22));
    }

    private void setupGoalsTable() {
        goalTypeColumn.setCellValueFactory(cellData -> {
            GoalType goalType = cellData.getValue().getGoalType();
            return new SimpleStringProperty(goalType.getDisplayName());
        });
        
        goalTargetColumn.setCellValueFactory(cellData -> {
            Goal goal = cellData.getValue();
            return new SimpleStringProperty(String.format("%.1f %s", 
                goal.getTargetValue(), goal.getGoalType().getUnit()));
        });
        
        goalCurrentColumn.setCellValueFactory(cellData -> {
            Goal goal = cellData.getValue();
            double current = calculateCurrentValue(goal);
            return new SimpleStringProperty(String.format("%.1f %s", 
                current, goal.getGoalType().getUnit()));
        });
        
        goalProgressColumn.setCellValueFactory(cellData -> {
            Goal goal = cellData.getValue();
            double progress = calculateProgress(goal);
            return new SimpleStringProperty(String.format("%.0f%%", progress));
        });
        
        goalDueColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDueDate()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        
        goalStatusColumn.setCellValueFactory(cellData -> {
            Goal goal = cellData.getValue();
            return new SimpleStringProperty(getGoalStatus(goal));
        });
        
        // Ustaw proporcjonalne szerokości dla tabeli celów
        goalTypeColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.22));
        goalTargetColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.18));
        goalCurrentColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.15));
        goalProgressColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.13));
        goalDueColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.16));
        goalStatusColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.16));
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

    private void loadGoalsData() {
        User currentUser = SessionManager.getCurrentUser();
        List<Goal> goals = goalService.getGoalsByUser(currentUser);
        goalsTable.setItems(FXCollections.observableArrayList(goals));
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

            MenuItem editItem = new MenuItem("Edytuj pomiar");
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
                        SessionManager.setAttribute("editedMeasurement", toEdit);
                        SceneManager.switchScene("/com/healthtracker/views/measurement_form.fxml", "Edytuj pomiar");
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Błąd podczas otwierania edycji").showAndWait();
                    }
                }
            });

            MenuItem deleteItem = new MenuItem("Usuń pomiar");
            deleteItem.setOnAction(event -> {
                MeasurementRow selectedRow = row.getItem();
                if (selectedRow == null) return;

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Czy na pewno chcesz usunąć ten pomiar?\n\n" + selectedRow.getSummary(),
                    ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Potwierdzenie usunięcia");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    List<Measurement> all = measurementService.getMeasurementsByUser(SessionManager.getCurrentUser());
                    Measurement toDelete = all.stream()
                            .filter(m -> m.getTimestamp().equals(selectedRow.getTimestamp()))
                            .findFirst()
                            .orElse(null);

                    if (toDelete != null) {
                        try {
                            measurementService.deleteMeasurement(toDelete);
                            loadData(); // odśwież tabelę po usunięciu
                            new Alert(Alert.AlertType.INFORMATION, "Pomiar został usunięty").showAndWait();
                        } catch (Exception e) {
                            e.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Błąd podczas usuwania: " + e.getMessage()).showAndWait();
                        }
                    }
                }
            });

            MenuItem detailsItem = new MenuItem("Szczegóły pomiaru");
            detailsItem.setOnAction(event -> {
                MeasurementRow selectedRow = row.getItem();
                if (selectedRow == null) return;
                showMeasurementDetail(selectedRow.getTimestamp());
            });

            contextMenu.getItems().addAll(editItem, deleteItem, new SeparatorMenuItem(), detailsItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings
                            .when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void setupActivitiesContextMenu() {
        activitiesTable.setRowFactory(tv -> {
            TableRow<Activity> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edytuj aktywność");
            editItem.setOnAction(event -> {
                Activity selectedActivity = row.getItem();
                if (selectedActivity == null) return;

                try {
                    SessionManager.setAttribute("editedActivity", selectedActivity);
                    SceneManager.switchScene("/com/healthtracker/views/activity_form.fxml", "Edytuj aktywność");
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Błąd podczas otwierania edycji").showAndWait();
                }
            });

            MenuItem deleteItem = new MenuItem("Usuń aktywność");
            deleteItem.setOnAction(event -> {
                Activity selectedActivity = row.getItem();
                if (selectedActivity == null) return;

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Czy na pewno chcesz usunąć tę aktywność?\n\n" + selectedActivity.getSummary(),
                    ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Potwierdzenie usunięcia");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    try {
                        activityService.deleteActivity(selectedActivity);
                        loadActivitiesData(); // Odśwież tabelę
                        new Alert(Alert.AlertType.INFORMATION, "Aktywność została usunięta").showAndWait();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Błąd podczas usuwania: " + e.getMessage()).showAndWait();
                    }
                }
            });

            MenuItem detailsItem = new MenuItem("Szczegóły aktywności");
            detailsItem.setOnAction(event -> {
                Activity selectedActivity = row.getItem();
                if (selectedActivity == null) return;
                showActivityDetails(selectedActivity);
            });

            contextMenu.getItems().addAll(editItem, deleteItem, new SeparatorMenuItem(), detailsItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings
                            .when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void setupMealsContextMenu() {
        mealsTable.setRowFactory(tv -> {
            TableRow<Meal> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edytuj posiłek");
            editItem.setOnAction(event -> {
                Meal selectedMeal = row.getItem();
                if (selectedMeal == null) return;

                try {
                    SessionManager.setAttribute("editedMeal", selectedMeal);
                    SceneManager.switchScene("/com/healthtracker/views/meal_form.fxml", "Edytuj posiłek");
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Błąd podczas otwierania edycji").showAndWait();
                }
            });

            MenuItem deleteItem = new MenuItem("Usuń posiłek");
            deleteItem.setOnAction(event -> {
                Meal selectedMeal = row.getItem();
                if (selectedMeal == null) return;

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Czy na pewno chcesz usunąć ten posiłek?\n\n" + 
                    selectedMeal.getType().getName() + " - " + selectedMeal.getCalories() + " kcal",
                    ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Potwierdzenie usunięcia");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    try {
                        mealService.deleteMeal(selectedMeal);
                        loadMealsData(); // Odśwież tabelę
                        new Alert(Alert.AlertType.INFORMATION, "Posiłek został usunięty").showAndWait();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Błąd podczas usuwania: " + e.getMessage()).showAndWait();
                    }
                }
            });

            MenuItem detailsItem = new MenuItem("Szczegóły posiłku");
            detailsItem.setOnAction(event -> {
                Meal selectedMeal = row.getItem();
                if (selectedMeal == null) return;
                showMealDetails(selectedMeal);
            });

            contextMenu.getItems().addAll(editItem, deleteItem, new SeparatorMenuItem(), detailsItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings
                            .when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void setupGoalsContextMenu() {
        goalsTable.setRowFactory(tv -> {
            TableRow<Goal> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edytuj cel");
            editItem.setOnAction(event -> {
                Goal selectedGoal = row.getItem();
                if (selectedGoal == null) return;

                try {
                    SessionManager.setAttribute("editedGoal", selectedGoal);
                    SceneManager.switchScene("/com/healthtracker/views/goal_form.fxml", "Edytuj cel");
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Błąd podczas otwierania edycji").showAndWait();
                }
            });

            MenuItem deleteItem = new MenuItem("Usuń cel");
            deleteItem.setOnAction(event -> {
                Goal selectedGoal = row.getItem();
                if (selectedGoal == null) return;

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Czy na pewno chcesz usunąć ten cel?\n\n" + 
                    selectedGoal.getGoalType().getDisplayName() + ": " + selectedGoal.getTargetValue(),
                    ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Potwierdzenie usunięcia");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    try {
                        goalService.deleteGoal(selectedGoal);
                        loadGoalsData();
                        new Alert(Alert.AlertType.INFORMATION, "Cel został usunięty").showAndWait();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Błąd podczas usuwania: " + e.getMessage()).showAndWait();
                    }
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

    private void showMeasurementDetail(LocalDateTime timestamp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/healthtracker/views/measurement_detail.fxml"));
            Parent root = loader.load();

            MeasurementDetailController controller = loader.getController();
            
            // Pobierz wszystkie pomiary użytkownika
            List<Measurement> allMeasurements = measurementService.getMeasurementsByUser(SessionManager.getCurrentUser());
            
            // Przekaż timestamp i wszystkie pomiary
            controller.setMeasurements(timestamp, allMeasurements);

            Stage stage = new Stage();
            stage.setTitle("Szczegóły pomiarów");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Błąd podczas otwierania szczegółów").showAndWait();
        }
    }

    private void showActivityDetails(Activity activity) {
        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Szczegóły aktywności");
        detailsAlert.setHeaderText(activity.getType().getCategory().getEmoji() + " " + activity.getType().getName());
        
        // USUŃ TO - CSS już jest załadowany globalnie
        // detailsAlert.getDialogPane().getStylesheets().add(
        //     getClass().getResource("/com/healthtracker/style.css").toExternalForm()
        // );
        // detailsAlert.getDialogPane().getStyleClass().add("alert");
        
        StringBuilder details = new StringBuilder();
        details.append("🕒 Czas trwania: ").append(activity.getDurationMinutes()).append(" min\n");
        
        if (activity.getDistanceKm() != null && activity.getDistanceKm() > 0) {
            if ("m".equals(activity.getType().getUnit())) {
                details.append("📏 Dystans: ").append(String.format("%.0f m", activity.getDistanceKm() * 1000)).append("\n");
            } else {
                details.append("📏 Dystans: ").append(String.format("%.2f km", activity.getDistanceKm())).append("\n");
            }
            details.append("⚡ Średnia prędkość: ").append(String.format("%.1f km/h", activity.calculateAvgSpeed())).append("\n");
        }
        
        if (activity.getCaloriesBurned() != null) {
            details.append("🔥 Kalorie: ").append(activity.getCaloriesBurned()).append(" kcal\n");
        }
        
        if (activity.getIntensity() != null) {
            details.append("💪 Intensywność: ").append(activity.getIntensity().getEmoji()).append(" ")
                    .append(activity.getIntensity().getDisplayName()).append("\n");
        }
        
        if (activity.getHeartRateAvg() != null) {
            details.append("❤️ Średnie tętno: ").append(activity.getHeartRateAvg()).append(" BPM\n");
        }
        
        if (activity.getHeartRateMax() != null) {
            details.append("💓 Maksymalne tętno: ").append(activity.getHeartRateMax()).append(" BPM\n");
        }
        
        details.append("📅 Data: ").append(activity.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        
        if (activity.getNotes() != null && !activity.getNotes().trim().isEmpty()) {
            details.append("\n\n📝 Notatki:\n").append(activity.getNotes());
        }
        
        detailsAlert.setContentText(details.toString());
        detailsAlert.showAndWait();
    }

    private void showMealDetails(Meal meal) {
        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Szczegóły posiłku");
        detailsAlert.setHeaderText("🍽️ " + meal.getType().getName());
        
        // Ustaw style dla alertu
        detailsAlert.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/healthtracker/style.css").toExternalForm()
        );
        detailsAlert.getDialogPane().getStyleClass().add("alert");
        
        StringBuilder details = new StringBuilder();
        details.append("🔥 Kalorie: ").append(meal.getCalories()).append(" kcal\n");
        details.append("📅 Data: ").append(meal.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n");
        details.append("\n📝 Opis:\n").append(meal.getDescription());
        
        detailsAlert.setContentText(details.toString());
        detailsAlert.showAndWait();
    }

    private double calculateCurrentValue(Goal goal) {
        User user = SessionManager.getCurrentUser();
        LocalDate now = LocalDate.now();
        
        return switch (goal.getGoalType()) {
            case WEIGHT_LOSS, TARGET_WEIGHT -> {
                // Znajdź najnowszy pomiar wagi
                List<Measurement> measurements = measurementService.getMeasurementsByUser(user);
                yield measurements.stream()
                    .filter(m -> m instanceof WeightMeasurement)
                    .map(m -> (WeightMeasurement) m)
                    .max(Comparator.comparing(Measurement::getTimestamp))
                    .map(WeightMeasurement::getWeight)
                    .orElse(0.0);
            }
            case ACTIVITY_HOURS -> { // POPRAWKA: Użyj prawidłowej nazwy z enum
                // Policz godziny aktywności w tym tygodniu
                LocalDateTime weekStart = now.atStartOfDay().minusDays(now.getDayOfWeek().getValue() - 1);
                List<Activity> activities = activityService.getActivitiesByUser(user);
                int totalMinutes = activities.stream()
                    .filter(a -> a.getTimestamp().isAfter(weekStart))
                    .mapToInt(Activity::getDurationMinutes)
                    .sum();
                yield totalMinutes / 60.0;
            }
            case DAILY_CALORIES -> { // POPRAWKA: Użyj prawidłowej nazwy z enum
                // Policz kalorie z dzisiaj
                LocalDateTime dayStart = now.atStartOfDay();
                List<Meal> meals = mealService.getMealsByUser(user);
                yield meals.stream()
                    .filter(m -> m.getTimestamp().isAfter(dayStart))
                    .mapToInt(Meal::getCalories)
                    .sum();
            }
            default -> 0.0; // POPRAWKA: Dodaj default case
        };
    }

    private double calculateProgress(Goal goal) {
        double current = calculateCurrentValue(goal);
        double target = goal.getTargetValue();
        
        return switch (goal.getGoalType()) {
            case WEIGHT_LOSS -> {
                // Dla utraty wagi - im mniej tym lepiej
                double startWeight = 80.0; // Można pobrać z pierwszego pomiaru
                if (current <= target) yield 100.0;
                yield Math.max(0, ((startWeight - current) / (startWeight - target)) * 100);
            }
            case TARGET_WEIGHT -> {
                // Dla wagi docelowej - im bliżej tym lepiej
                double diff = Math.abs(current - target);
                yield Math.max(0, 100 - (diff * 10)); // Każdy kg różnicy = -10%
            }
            default -> Math.min(100, (current / target) * 100);
        };
    }

    private String getGoalStatus(Goal goal) {
        double progress = calculateProgress(goal);
        LocalDate now = LocalDate.now();
        
        if (now.isAfter(goal.getDueDate())) {
            return progress >= 100 ? "✅ Osiągnięty" : "❌ Nieukończony";
        } else if (progress >= 100) {
            return "🎉 Ukończony";
        } else if (progress >= 75) {
            return "🟡 Blisko celu";
        } else if (progress >= 50) {
            return "🔵 W trakcie";
        } else {
            return "🔴 Początek";
        }
    }

    // Export/Import methods for Activities
    @FXML
    private void onExportActivitiesClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik CSV - Aktywności");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("aktywnosci.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                exportImportService.exportActivitiesToCsv(SessionManager.getCurrentUser(), file);
                new Alert(Alert.AlertType.INFORMATION, "Aktywności zostały wyeksportowane.").showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Błąd eksportu: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    private void onImportActivitiesClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik CSV - Aktywności");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                exportImportService.importActivitiesFromCsv(SessionManager.getCurrentUser(), file);
                new Alert(Alert.AlertType.INFORMATION, "Import aktywności zakończony!").showAndWait();
                loadActivitiesData();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Błąd importu: " + e.getMessage()).showAndWait();
            }
        }
    }

    // Export/Import methods for Meals
    @FXML
    private void onExportMealsClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik CSV - Posiłki");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("posilki.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                exportImportService.exportMealsToCsv(SessionManager.getCurrentUser(), file);
                new Alert(Alert.AlertType.INFORMATION, "Posiłki zostały wyeksportowane.").showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Błąd eksportu: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    private void onImportMealsClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik CSV - Posiłki");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                exportImportService.importMealsFromCsv(SessionManager.getCurrentUser(), file);
                new Alert(Alert.AlertType.INFORMATION, "Import posiłków zakończony!").showAndWait();
                loadMealsData();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Błąd importu: " + e.getMessage()).showAndWait();
            }
        }
    }

    // Rename existing methods for consistency
    @FXML
    private void onExportMeasurementsClicked() {
        onExportClicked();
    }

    @FXML
    private void onImportMeasurementsClicked() {
        onImportClicked();
    }

    @FXML
    private void onAddGoal() {
        SceneManager.switchScene("/com/healthtracker/views/goal_form.fxml", "Nowy cel");
    }

    @FXML
    private void onProfileClicked() {
        SceneManager.switchScene("/com/healthtracker/views/profile.fxml", "Profil użytkownika");
    }

    @FXML
    private void onStatisticsClicked() {
        SceneManager.switchScene("/com/healthtracker/views/statistics.fxml", "Statystyki");
    }

    @FXML
    private void onLogoutClicked() {
        SessionManager.logout();
        SceneManager.switchScene("/com/healthtracker/views/login.fxml", "Logowanie");
    }

    @FXML
    private void onAddActivity() {
        SceneManager.switchScene("/com/healthtracker/views/activity_form.fxml", "Nowa aktywność");
    }

    @FXML
    private void onAddMeal() {
        SceneManager.switchScene("/com/healthtracker/views/meal_form.fxml", "Nowy posiłek");
    }
}
