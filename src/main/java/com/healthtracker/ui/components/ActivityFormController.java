package com.healthtracker.ui.components;

import com.healthtracker.dao.impl.ActivityTypeDaoImpl;
import com.healthtracker.model.*;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.impl.ActivityServiceImpl;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.util.SessionManager;
import com.healthtracker.ui.SceneManager;
import com.healthtracker.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Kontroler formularza dodawania aktywności fizycznej.
 */
public class ActivityFormController {
    @FXML private TextField durationField;
    @FXML private TextField distanceField;
    @FXML private TextField caloriesField;
    @FXML private TextField heartRateAvgField;
    @FXML private TextField heartRateMaxField;
    @FXML private TextArea notesField;
    @FXML private ComboBox<ActivityType> typeCombo;
    @FXML private ComboBox<IntensityLevel> intensityCombo;
    @FXML private Label distanceLabel;
    @FXML private VBox distanceGroup;
    @FXML private Button saveButton;

    private final ActivityService activityService = new ActivityServiceImpl();
    private final ActivityTypeDaoImpl activityTypeDao = new ActivityTypeDaoImpl();
    private final MeasurementService measurementService = new MeasurementServiceImpl(); // Dodaj to
    private Activity existingActivity; // Dodaj to pole

    @FXML
    private void initialize() {
        setupActivityTypes();
        setupIntensityLevels();
        setupFieldValidation();
        
        // Sprawdź czy edytujemy istniejącą aktywność
        Activity editedActivity = (Activity) SessionManager.getAttribute("editedActivity");
        if (editedActivity != null) {
            setActivity(editedActivity);
            SessionManager.removeAttribute("editedActivity");
            saveButton.setText("Zaktualizuj aktywność");
        }
    }

    public void setActivity(Activity activity) {
        this.existingActivity = activity;
        
        // Wypełnij pola danymi z aktywności
        typeCombo.setValue(activity.getType());
        durationField.setText(String.valueOf(activity.getDurationMinutes()));
        
        if (activity.getDistanceKm() != null && activity.getDistanceKm() > 0) {
            if ("m".equals(activity.getType().getUnit())) {
                distanceField.setText(String.valueOf((int)(activity.getDistanceKm() * 1000)));
            } else {
                distanceField.setText(String.format("%.2f", activity.getDistanceKm()));
            }
        }
        
        if (activity.getCaloriesBurned() != null) {
            caloriesField.setText(String.valueOf(activity.getCaloriesBurned()));
        }
        
        if (activity.getHeartRateAvg() != null) {
            heartRateAvgField.setText(String.valueOf(activity.getHeartRateAvg()));
        }
        
        if (activity.getHeartRateMax() != null) {
            heartRateMaxField.setText(String.valueOf(activity.getHeartRateMax()));
        }
        
        if (activity.getNotes() != null) {
            notesField.setText(activity.getNotes());
        }
        
        if (activity.getIntensity() != null) {
            intensityCombo.setValue(activity.getIntensity());
        }
        
        // Zaktualizuj widoczność pola dystansu
        updateDistanceFieldVisibility(activity.getType());
    }

    private void setupActivityTypes() {
        // Inicjalizuj domyślne typy jeśli baza jest pusta
        if (activityTypeDao.findAll().isEmpty()) {
            initializeDefaultActivityTypes();
        }
        
        typeCombo.getItems().addAll(activityTypeDao.findAll());
        typeCombo.setConverter(new StringConverter<ActivityType>() {
            @Override
            public String toString(ActivityType type) {
                return type != null ? 
                    type.getCategory().getEmoji() + " " + type.getName() + 
                    (type.isRequiresDistance() ? " (" + type.getUnit() + ")" : "") : "";
            }

            @Override
            public ActivityType fromString(String string) {
                return null;
            }
        });
        
        // Nasłuchuj zmian w typie aktywności
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateDistanceFieldVisibility(newVal);
            updateCaloriesEstimate();
        });
        
        updateDistanceFieldVisibility(null);
    }

    private void setupIntensityLevels() {
        intensityCombo.getItems().addAll(IntensityLevel.values());
        intensityCombo.setConverter(new StringConverter<IntensityLevel>() {
            @Override
            public String toString(IntensityLevel level) {
                return level != null ? level.getEmoji() + " " + level.getDisplayName() : "";
            }

            @Override
            public IntensityLevel fromString(String string) {
                return null;
            }
        });
        
        // Domyślnie ustaw średnią intensywność
        intensityCombo.setValue(IntensityLevel.MODERATE);
    }

    private void setupFieldValidation() {
        // Auto-kalkulacja kalorii gdy zmieni się czas trwania
        durationField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateCaloriesEstimate();
        });
        
        // Walidacja liczb w polach tętna
        heartRateAvgField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!ValidationUtil.matches(newVal, "\\d*")) {
                heartRateAvgField.setText(ValidationUtil.keepOnlyDigits(newVal));
            }
        });
        
        heartRateMaxField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!ValidationUtil.matches(newVal, "\\d*")) {
                heartRateMaxField.setText(ValidationUtil.keepOnlyDigits(newVal));
            }
        });
    }

    private void updateDistanceFieldVisibility(ActivityType activityType) {
        boolean requiresDistance = activityType != null && activityType.isRequiresDistance();
        
        distanceField.setVisible(requiresDistance);
        distanceField.setManaged(requiresDistance);
        distanceLabel.setVisible(requiresDistance);
        distanceLabel.setManaged(requiresDistance);
        distanceGroup.setVisible(requiresDistance);
        distanceGroup.setManaged(requiresDistance);
        
        if (!requiresDistance) {
            distanceField.clear();
        } else if (activityType != null) {
            distanceField.setPromptText("Wprowadź dystans w " + activityType.getUnit());
            distanceLabel.setText("Dystans (" + activityType.getUnit() + ")");
        }
    }

    private void updateCaloriesEstimate() {
        try {
            ActivityType type = typeCombo.getValue();
            String durationText = durationField.getText().trim();
            
            if (type != null && !durationText.isEmpty()) {
                int duration = Integer.parseInt(durationText);
                User user = SessionManager.getCurrentUser();
                
                // Szacuj wagę użytkownika (można by to pobrać z ostatniego pomiaru)
                double estimatedWeight = 70.0; // Domyślna waga
                
                // Tymczasowy obiekt Activity do kalkulacji
                Activity tempActivity = new Activity();
                tempActivity.setType(type);
                tempActivity.setDurationMinutes(duration);
                
                int estimatedCalories = tempActivity.estimateCaloriesBurn(estimatedWeight);
                caloriesField.setPromptText("Szacowane: " + estimatedCalories + " kcal");
            }
        } catch (NumberFormatException ignored) {
            // Ignoruj błędy parsowania podczas wpisywania
        }
    }

    @FXML
    private void onSaveClicked() {
        try {
            // Walidacja podstawowych pól
            ActivityType selectedType = typeCombo.getValue();
            if (selectedType == null) {
                showError("Wybierz typ aktywności");
                return;
            }

            String durationText = durationField.getText().trim();
            if (durationText.isEmpty()) {
                showError("Wprowadź czas trwania");
                return;
            }

            int duration = Integer.parseInt(durationText);
            
            // Parsowanie dystansu jeśli wymagany
            Double distance = null;
            if (selectedType.isRequiresDistance()) {
                String distanceText = distanceField.getText().trim();
                if (distanceText.isEmpty()) {
                    showError("Wprowadź dystans");
                    return;
                }
                
                distance = Double.parseDouble(distanceText);
                
                // Konwersja na kilometry jeśli jednostka to metry
                if ("m".equals(selectedType.getUnit())) {
                    distance = distance / 1000.0;
                }
            }
            
            // Parsowanie opcjonalnych pól
            Integer calories = parseOptionalInteger(caloriesField.getText().trim());
            Integer heartRateAvg = parseOptionalInteger(heartRateAvgField.getText().trim());
            Integer heartRateMax = parseOptionalInteger(heartRateMaxField.getText().trim());
            String notes = notesField.getText().trim();
            IntensityLevel intensity = intensityCombo.getValue();

            // Walidacja za pomocą ValidationUtil
            if (heartRateAvg != null && !ValidationUtil.isHeartRateValid(heartRateAvg)) {
                showError("Średnie tętno powinno być między 40 a 220 BPM");
                return;
            }
            
            if (heartRateMax != null && !ValidationUtil.isHeartRateValid(heartRateMax)) {
                showError("Maksymalne tętno powinno być między 40 a 220 BPM");
                return;
            }

            if (!ValidationUtil.isDurationValid(duration)) {
                showError("Czas trwania musi być większy od 0");
                return;
            }

            // Walidacja dystansu jeśli wymagany
            if (selectedType.isRequiresDistance() && !ValidationUtil.isDistanceValid(distance)) {
                showError("Dystans musi być większy od 0");
                return;
            }
            
            if (heartRateAvg != null && heartRateMax != null && heartRateAvg > heartRateMax) {
                showError("Średnie tętno nie może być wyższe od maksymalnego");
                return;
            }

            Activity activity;
            boolean isEditing = existingActivity != null;

            if (isEditing) {
                // Tryb edycji - zaktualizuj istniejącą aktywność
                activity = existingActivity;
                activity.setType(selectedType);
                activity.setDurationMinutes(duration);
                activity.setDistanceKm(distance);
                activity.setCaloriesBurned(calories);
                activity.setHeartRateAvg(heartRateAvg);
                activity.setHeartRateMax(heartRateMax);
                activity.setNotes(notes.isEmpty() ? null : notes);
                activity.setIntensity(intensity);
                // Nie zmieniamy timestamp przy edycji
            } else {
                // Tryb dodawania - stwórz nową aktywność
                activity = new Activity();
                activity.setUser(SessionManager.getCurrentUser());
                activity.setType(selectedType);
                activity.setDurationMinutes(duration);
                activity.setDistanceKm(distance);
                activity.setCaloriesBurned(calories);
                activity.setHeartRateAvg(heartRateAvg);
                activity.setHeartRateMax(heartRateMax);
                activity.setNotes(notes.isEmpty() ? null : notes);
                activity.setIntensity(intensity);
                activity.setTimestamp(LocalDateTime.now());
            }

            // Auto-kalkulacja jeśli nie podano kalorii
            if (calories == null) {
                double userWeight = getUserWeight();
                activity.setCaloriesBurned(activity.estimateCaloriesBurn(userWeight));
            }

            if (isEditing) {
                activityService.updateActivity(activity);
                showSuccess("Aktywność została zaktualizowana!\n" + 
                           "Podsumowanie: " + activity.getSummary());
            } else {
                activityService.addActivity(activity);
                showSuccess("Aktywność została zapisana!\n" + 
                           "Podsumowanie: " + activity.getSummary());
            }
            
            SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");

        } catch (NumberFormatException e) {
            showError("Wprowadź poprawne wartości liczbowe");
        } catch (Exception e) {
            showError("Błąd zapisu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Integer parseOptionalInteger(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double getUserWeight() {
        try {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                return 70.0;
            }

            List<Measurement> measurements = measurementService.getMeasurementsByUser(currentUser);
            
            Optional<WeightMeasurement> latestWeight = measurements.stream()
                .filter(m -> m instanceof WeightMeasurement)
                .map(m -> (WeightMeasurement) m)
                .max(Comparator.comparing(Measurement::getTimestamp));
            
            return latestWeight.map(WeightMeasurement::getWeight).orElse(70.0);
        } catch (Exception e) {
            System.err.println("Błąd podczas pobierania wagi użytkownika: " + e.getMessage());
            return 70.0;
        }
    }

    private void initializeDefaultActivityTypes() {
        // Inicjalizuj domyślne typy aktywności
        createAndSaveActivityType("Bieganie", "km", true, ActivityCategory.CARDIO, "Bieganie na świeżym powietrzu lub bieżni");
        createAndSaveActivityType("Rower", "km", true, ActivityCategory.CARDIO, "Jazda na rowerze");
        createAndSaveActivityType("Chodzenie", "km", true, ActivityCategory.CARDIO, "Spacer lub marsz");
        createAndSaveActivityType("Pływanie", "m", true, ActivityCategory.WATER_SPORTS, "Pływanie w basenie lub akwenie");
        createAndSaveActivityType("Siłownia", "serie", false, ActivityCategory.STRENGTH, "Trening siłowy");
        createAndSaveActivityType("Joga", "min", false, ActivityCategory.FLEXIBILITY, "Ćwiczenia jogi");
        createAndSaveActivityType("Aerobik", "min", false, ActivityCategory.CARDIO, "Ćwiczenia aerobowe");
        createAndSaveActivityType("Pilates", "min", false, ActivityCategory.FLEXIBILITY, "Ćwiczenia pilates");
        createAndSaveActivityType("Tenis", "sety", false, ActivityCategory.TEAM_SPORTS, "Gra w tenisa");
    }

    private void createAndSaveActivityType(String name, String unit, boolean requiresDistance, 
                                         ActivityCategory category, String description) {
        ActivityType type = new ActivityType(name, unit, requiresDistance, category);
        type.setDescription(description);
        activityTypeDao.save(type);
    }

    @FXML
    private void onCancelClicked() {
        SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Błąd");
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Sukces");
        alert.showAndWait();
    }
}
