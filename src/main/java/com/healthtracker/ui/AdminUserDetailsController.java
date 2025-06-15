package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.*;
import com.healthtracker.service.impl.*;
import com.healthtracker.model.MeasurementRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Kontroler szczegółowego widoku danych użytkownika dla administratora.
 */
public class AdminUserDetailsController {

    // User info
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label userStatsLabel;

    // TabPane
    @FXML private TabPane dataTabPane;

    // Measurements tab
    @FXML private TableView<MeasurementRow> measurementsTable;
    @FXML private TableColumn<MeasurementRow, String> bpColumn;
    @FXML private TableColumn<MeasurementRow, String> hrColumn;
    @FXML private TableColumn<MeasurementRow, String> weightColumn;
    @FXML private TableColumn<MeasurementRow, String> summaryColumn;
    @FXML private TableColumn<MeasurementRow, LocalDateTime> timestampColumn;

    // Activities tab
    @FXML private TableView<Activity> activitiesTable;
    @FXML private TableColumn<Activity, String> activityTypeColumn;
    @FXML private TableColumn<Activity, String> durationColumn;
    @FXML private TableColumn<Activity, String> distanceColumn;
    @FXML private TableColumn<Activity, String> activityCaloriesColumn;
    @FXML private TableColumn<Activity, String> intensityColumn;
    @FXML private TableColumn<Activity, LocalDateTime> activityDateColumn;

    // Meals tab
    @FXML private TableView<Meal> mealsTable;
    @FXML private TableColumn<Meal, String> mealTypeColumn;
    @FXML private TableColumn<Meal, String> mealCaloriesColumn;
    @FXML private TableColumn<Meal, String> descriptionColumn;
    @FXML private TableColumn<Meal, LocalDateTime> mealDateColumn;

    // Goals tab
    @FXML private TableView<Goal> goalsTable;
    @FXML private TableColumn<Goal, String> goalTypeColumn;
    @FXML private TableColumn<Goal, String> goalTargetColumn;
    @FXML private TableColumn<Goal, String> goalCurrentColumn;
    @FXML private TableColumn<Goal, String> goalProgressColumn;
    @FXML private TableColumn<Goal, String> goalDueColumn;
    @FXML private TableColumn<Goal, String> goalStatusColumn;

    // Summary
    @FXML private VBox summaryBox;
    @FXML private Label measurementsSummaryLabel;
    @FXML private Label activitiesSummaryLabel;
    @FXML private Label mealsSummaryLabel;
    @FXML private Label goalsSummaryLabel;

    private User currentUser;
    private final MeasurementService measurementService = new MeasurementServiceImpl();
    private final ActivityService activityService = new ActivityServiceImpl();
    private final MealService mealService = new MealServiceImpl();
    private final GoalService goalService = new GoalServiceImpl();

    @FXML
    public void initialize() {
        setupTables();
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadUserInfo();
        loadAllData();
        generateSummary();
    }

    private void setupTables() {
        setupMeasurementsTable();
        setupActivitiesTable();
        setupMealsTable();
        setupGoalsTable();
        
        // Dodaj wspólne klasy CSS dla wszystkich tabel
        measurementsTable.getStyleClass().add("data-table");
        activitiesTable.getStyleClass().add("data-table");
        mealsTable.getStyleClass().add("data-table");
        goalsTable.getStyleClass().add("data-table");
    }

    private void setupMeasurementsTable() {
        timestampColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getTimestamp()));
        timestampColumn.setCellFactory(column -> new TableCell<MeasurementRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }
            }
        });
        
        timestampColumn.setComparator(Comparator.reverseOrder());
        measurementsTable.getSortOrder().add(timestampColumn);
        
        bpColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBloodPressure()));
        hrColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHeartRate()));
        weightColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWeight()));
        summaryColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSummary()));

        // Proporcjonalne szerokości
        timestampColumn.prefWidthProperty().bind(measurementsTable.widthProperty().multiply(0.2));
        bpColumn.prefWidthProperty().bind(measurementsTable.widthProperty().multiply(0.15));
        hrColumn.prefWidthProperty().bind(measurementsTable.widthProperty().multiply(0.1));
        weightColumn.prefWidthProperty().bind(measurementsTable.widthProperty().multiply(0.1));
        summaryColumn.prefWidthProperty().bind(measurementsTable.widthProperty().multiply(0.45));
    }

    private void setupActivitiesTable() {
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

        activityCaloriesColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCaloriesBurned() != null ? 
                cellData.getValue().getCaloriesBurned() + " kcal" : "-"));

        intensityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getIntensity() != null ? 
                cellData.getValue().getIntensity().getEmoji() + " " + cellData.getValue().getIntensity().getDisplayName() : "-"));

        activityDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getTimestamp()));
        activityDateColumn.setCellFactory(column -> new TableCell<Activity, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }
            }
        });
        
        activityDateColumn.setComparator(Comparator.reverseOrder());
        activitiesTable.getSortOrder().add(activityDateColumn);

        // Proporcjonalne szerokości
        activityTypeColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.25));
        durationColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.12));
        distanceColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.12));
        activityCaloriesColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.12));
        intensityColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.15));
        activityDateColumn.prefWidthProperty().bind(activitiesTable.widthProperty().multiply(0.24));
    }

    private void setupMealsTable() {
        mealTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getType().getName()));

        mealCaloriesColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCalories() + " kcal"));

        descriptionColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDescription()));

        mealDateColumn.setCellValueFactory(cellData ->
            new SimpleObjectProperty<>(cellData.getValue().getTimestamp()));
        mealDateColumn.setCellFactory(column -> new TableCell<Meal, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }
            }
        });
        
        mealDateColumn.setComparator(Comparator.reverseOrder());
        mealsTable.getSortOrder().add(mealDateColumn);

        // Proporcjonalne szerokości
        mealTypeColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.2));
        mealCaloriesColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.15));
        descriptionColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.4));
        mealDateColumn.prefWidthProperty().bind(mealsTable.widthProperty().multiply(0.25));
    }

    private void setupGoalsTable() {
        goalTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getGoalType().getDisplayName()));

        goalTargetColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(String.valueOf(cellData.getValue().getTargetValue()) + " " + 
                cellData.getValue().getGoalType().getUnit()));

        goalCurrentColumn.setCellValueFactory(cellData -> {
            double current = calculateCurrentValue(cellData.getValue());
            return new SimpleStringProperty(String.format("%.1f %s", current, 
                cellData.getValue().getGoalType().getUnit()));
        });

        goalProgressColumn.setCellValueFactory(cellData -> {
            double progress = calculateProgress(cellData.getValue());
            return new SimpleStringProperty(String.format("%.0f%%", progress));
        });

        goalDueColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDueDate()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

        goalStatusColumn.setCellValueFactory(cellData -> {
            Goal goal = cellData.getValue();
            return new SimpleStringProperty(getGoalStatus(goal));
        });

        // Proporcjonalne szerokości
        goalTypeColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.22));
        goalTargetColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.15));
        goalCurrentColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.15));
        goalProgressColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.12));
        goalDueColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.16));
        goalStatusColumn.prefWidthProperty().bind(goalsTable.widthProperty().multiply(0.2));
    }

    private void loadUserInfo() {
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        roleLabel.setText(currentUser.getRole().toString());
        
        // Dodaj wyświetlanie daty utworzenia
        if (currentUser.getCreatedAt() != null) {
            createdAtLabel.setText(currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        } else {
            createdAtLabel.setText("Nieznana");
        }
    }

    private void loadAllData() {
        loadMeasurementsData();
        loadActivitiesData();
        loadMealsData();
        loadGoalsData();
    }

    private void loadMeasurementsData() {
        try {
            List<Measurement> measurements = measurementService.getMeasurementsByUser(currentUser);
            
            // Grupuj pomiary według timestamp
            Map<LocalDateTime, List<Measurement>> groupedMeasurements = measurements.stream()
                .collect(Collectors.groupingBy(Measurement::getTimestamp));
            
            List<MeasurementRow> rows = new ArrayList<>();
            
            for (Map.Entry<LocalDateTime, List<Measurement>> entry : groupedMeasurements.entrySet()) {
                LocalDateTime timestamp = entry.getKey();
                List<Measurement> measurementsAtTime = entry.getValue();
                
                MeasurementRow row = new MeasurementRow(timestamp);
                StringBuilder summaryBuilder = new StringBuilder();
                
                for (Measurement measurement : measurementsAtTime) {
                    if (measurement instanceof BloodPressureMeasurement) {
                        BloodPressureMeasurement bp = (BloodPressureMeasurement) measurement;
                        row.setBloodPressure(bp.getSystolic() + "/" + bp.getDiastolic() + " mmHg");
                        if (summaryBuilder.length() > 0) summaryBuilder.append(", ");
                        summaryBuilder.append("Ciśnienie: ").append(bp.getSystolic()).append("/").append(bp.getDiastolic());
                    }
                    
                    if (measurement instanceof HeartRateMeasurement) {
                        HeartRateMeasurement hr = (HeartRateMeasurement) measurement;
                        row.setHeartRate(hr.getBpm() + " bpm");
                        if (summaryBuilder.length() > 0) summaryBuilder.append(", ");
                        summaryBuilder.append("Tętno: ").append(hr.getBpm()).append(" bpm");
                    }
                    
                    if (measurement instanceof WeightMeasurement) {
                        WeightMeasurement w = (WeightMeasurement) measurement;
                        row.setWeight(w.getWeight() + " kg");
                        if (summaryBuilder.length() > 0) summaryBuilder.append(", ");
                        summaryBuilder.append("Waga: ").append(w.getWeight()).append(" kg");
                    }
                    
                    // Dodaj summary z poszczególnych pomiarów
                    if (measurement.getSummary() != null && !measurement.getSummary().trim().isEmpty()) {
                        if (summaryBuilder.length() > 0) summaryBuilder.append(" | ");
                        summaryBuilder.append(measurement.getSummary());
                    }
                }
                
                row.setSummary(summaryBuilder.toString());
                rows.add(row);
            }
            
            // Sortuj według daty (najnowsze na górze)
            rows.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));
            
            measurementsTable.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadActivitiesData() {
        try {
            List<Activity> activities = activityService.getActivitiesByUser(currentUser);
            activities.sort(Comparator.comparing(Activity::getTimestamp).reversed());
            activitiesTable.setItems(FXCollections.observableArrayList(activities));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMealsData() {
        try {
            List<Meal> meals = mealService.getMealsByUser(currentUser);
            meals.sort(Comparator.comparing(Meal::getTimestamp).reversed());
            mealsTable.setItems(FXCollections.observableArrayList(meals));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGoalsData() {
        try {
            List<Goal> goals = goalService.getGoalsByUser(currentUser);
            goals.sort(Comparator.comparing(Goal::getDueDate));
            goalsTable.setItems(FXCollections.observableArrayList(goals));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateSummary() {
        try {
            // Podsumowanie pomiarów
            List<Measurement> measurements = measurementService.getMeasurementsByUser(currentUser);

            long uniqueMeasurementSessions = measurements.stream()
            .map(Measurement::getTimestamp)
            .distinct()
            .count();

            measurementsSummaryLabel.setText(String.format(
                "📊 Pomiary: %d | Ostatni: %s",
                uniqueMeasurementSessions,
                measurements.isEmpty() ? "brak" : 
                measurements.stream()
                .max(Comparator.comparing(Measurement::getTimestamp))
                .get().getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        ));

            // Podsumowanie aktywności
            List<Activity> activities = activityService.getActivitiesByUser(currentUser);
            int totalMinutes = activities.stream().mapToInt(Activity::getDurationMinutes).sum();
            Integer totalCalories = activities.stream()
                .filter(a -> a.getCaloriesBurned() != null)
                .mapToInt(Activity::getCaloriesBurned).sum();
            
            activitiesSummaryLabel.setText(String.format(
                "🏃 Aktywności: %d | Czas: %dh %dmin | Kalorie: %d kcal",
                activities.size(),
                totalMinutes / 60,
                totalMinutes % 60,
                totalCalories
            ));

            // Podsumowanie posiłków
            List<Meal> meals = mealService.getMealsByUser(currentUser);
            int totalMealCalories = meals.stream().mapToInt(Meal::getCalories).sum();
            
            mealsSummaryLabel.setText(String.format(
                "🍽 Posiłki: %d | Łącznie kalorii: %d kcal",
                meals.size(),
                totalMealCalories
            ));

            // Podsumowanie celów
            List<Goal> goals = goalService.getGoalsByUser(currentUser);
            long completedGoals = goals.stream()
                .filter(g -> calculateProgress(g) >= 100)
                .count();
            
            goalsSummaryLabel.setText(String.format(
                "🎯 Cele: %d | Ukończone: %d | Aktywne: %d",
                goals.size(),
                completedGoals,
                goals.size() - completedGoals
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Pomocnicze metody z UserDashboardController
    private double calculateCurrentValue(Goal goal) {
        // Uproszczona wersja - możesz rozszerzyć podobnie jak w UserDashboardController
        return switch (goal.getGoalType()) {
            case WEIGHT_LOSS, TARGET_WEIGHT -> {
                List<Measurement> measurements = measurementService.getMeasurementsByUser(currentUser);
                yield measurements.stream()
                    .filter(m -> m instanceof WeightMeasurement)
                    .map(m -> (WeightMeasurement) m)
                    .max(Comparator.comparing(Measurement::getTimestamp))
                    .map(WeightMeasurement::getWeight)
                    .orElse(0.0);
            }
            default -> 0.0;
        };
    }

    private double calculateProgress(Goal goal) {
        double current = calculateCurrentValue(goal);
        double target = goal.getTargetValue();
        
        if (target == 0) return 0.0;
        
        return switch (goal.getGoalType()) {
            case WEIGHT_LOSS -> {
                double startWeight = 80.0; // Można pobrać z pierwszego pomiaru
                if (current <= target) yield 100.0;
                yield Math.max(0, ((startWeight - current) / (startWeight - target)) * 100);
            }
            case TARGET_WEIGHT -> {
                double diff = Math.abs(current - target);
                yield Math.max(0, 100 - (diff * 10));
            }
            default -> Math.min(100, (current / target) * 100);
        };
    }

    private String getGoalStatus(Goal goal) {
        double progress = calculateProgress(goal);
        java.time.LocalDate now = java.time.LocalDate.now();
        
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

    @FXML
    private void onCloseClicked() {
        ((Stage) usernameLabel.getScene().getWindow()).close();
    }
}