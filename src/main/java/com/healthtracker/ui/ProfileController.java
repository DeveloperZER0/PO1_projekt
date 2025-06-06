package com.healthtracker.ui;

import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.MealService;
import com.healthtracker.service.GoalService;
import com.healthtracker.service.impl.UserServiceImpl;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.service.impl.ActivityServiceImpl;
import com.healthtracker.service.impl.MealServiceImpl;
import com.healthtracker.service.impl.GoalServiceImpl;
import com.healthtracker.util.SessionManager;
import com.healthtracker.ui.SceneManager;
import com.healthtracker.util.HashUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ProfileController {

    // Informacje o koncie
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label statisticsLabel;

    // Sekcja zmiany hasła
    @FXML private VBox changePasswordSection;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changePasswordButton;

    // Sekcja zarządzania danymi
    @FXML private VBox dataManagementSection;
    @FXML private Button clearDataButton;
    @FXML private Button deleteAccountButton;

    // Sekcja potwierdzenia hasła dla krytycznych operacji
    @FXML private VBox confirmationSection;
    @FXML private PasswordField confirmationPasswordField;
    @FXML private Label confirmationLabel;
    @FXML private Button confirmActionButton;
    @FXML private Button cancelActionButton;

    private final UserService userService = new UserServiceImpl();
    private final MeasurementService measurementService = new MeasurementServiceImpl();
    private final ActivityService activityService = new ActivityServiceImpl();
    private final MealService mealService = new MealServiceImpl();
    private final GoalService goalService = new GoalServiceImpl();

    private String pendingAction = ""; // "DELETE_ACCOUNT" lub "CLEAR_DATA"

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        setupUserInfo(user);
        setupStatistics(user);
        hideConfirmationSection();
    }

    private void setupUserInfo(User user) {
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole().toString());
        
        // Jeśli User ma pole createdAt, użyj go; w przeciwnym razie pozostaw puste
        if (user.getCreatedAt() != null) {
            createdAtLabel.setText(user.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } else {
            createdAtLabel.setText("Nieznana");
        }
    }

    private void setupStatistics(User user) {
        try {
            int measurementsCount = measurementService.getMeasurementsByUser(user).size();
            int activitiesCount = activityService.getActivitiesByUser(user).size();
            int mealsCount = mealService.getMealsByUser(user).size();
            int goalsCount = goalService.getGoalsByUser(user).size();

            statisticsLabel.setText(String.format(
                "📊 Pomiary: %d | 🏃 Aktywności: %d | 🍽 Posiłki: %d | 🎯 Cele: %d",
                measurementsCount, activitiesCount, mealsCount, goalsCount
            ));
        } catch (Exception e) {
            statisticsLabel.setText("Błąd podczas ładowania statystyk");
            e.printStackTrace();
        }
    }

    @FXML
    private void onChangePasswordClicked() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Walidacja
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Wszystkie pola hasła muszą być wypełnione");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Nowe hasła nie są identyczne");
            return;
        }

        if (!userService.isPasswordValid(newPassword)) {
            showError("Nowe hasło nie spełnia wymagań bezpieczeństwa");
            return;
        }

        // Sprawdź aktualne hasło
        User currentUser = SessionManager.getCurrentUser();
        if (!HashUtil.verify(currentPassword, currentUser.getPasswordHash())) {
            showError("Aktualne hasło jest nieprawidłowe");
            return;
        }

        try {
            // Zmień hasło
            currentUser.setPasswordHash(HashUtil.hash(newPassword));
            userService.updateUser(currentUser);

            // Wyczyść pola
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            showSuccess("Hasło zostało zmienione pomyślnie!");

        } catch (Exception e) {
            showError("Błąd podczas zmiany hasła: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onClearDataClicked() {
        pendingAction = "CLEAR_DATA";
        confirmationLabel.setText("Wprowadź hasło, aby potwierdzić usunięcie wszystkich danych:");
        showConfirmationSection();
    }

    @FXML
    private void onDeleteAccountClicked() {
        pendingAction = "DELETE_ACCOUNT";
        confirmationLabel.setText("Wprowadź hasło, aby potwierdzić usunięcie konta:");
        showConfirmationSection();
    }

    @FXML
    private void onConfirmActionClicked() {
        String password = confirmationPasswordField.getText();
        
        if (password.isEmpty()) {
            showError("Wprowadź hasło");
            return;
        }

        User currentUser = SessionManager.getCurrentUser();
        if (!HashUtil.verify(password, currentUser.getPasswordHash())) {
            showError("Nieprawidłowe hasło");
            return;
        }

        try {
            switch (pendingAction) {
                case "CLEAR_DATA" -> {
                    clearAllUserData(currentUser);
                    hideConfirmationSection();
                    setupStatistics(currentUser); // Odśwież statystyki
                    showSuccess("Wszystkie dane zostały usunięte!");
                }
                case "DELETE_ACCOUNT" -> {
                    deleteUserAccount(currentUser);
                    // Po usunięciu konta przekieruj do logowania
                    SessionManager.logout();
                    SceneManager.switchScene("/com/healthtracker/views/login.fxml", "Logowanie");
                    showSuccess("Konto zostało usunięte");
                }
            }
        } catch (Exception e) {
            showError("Błąd podczas wykonywania operacji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelActionClicked() {
        hideConfirmationSection();
    }

    private void clearAllUserData(User user) throws Exception {
        // Usuń wszystkie dane użytkownika, ale zostaw konto
        measurementService.getMeasurementsByUser(user).forEach(measurementService::deleteMeasurement);
        activityService.getActivitiesByUser(user).forEach(activityService::deleteActivity);
        mealService.getMealsByUser(user).forEach(mealService::deleteMeal);
        goalService.getGoalsByUser(user).forEach(goalService::deleteGoal);
    }

    private void deleteUserAccount(User user) throws Exception {
        // Usuń użytkownika i wszystkie powiązane dane
        userService.deleteUser(user);
    }

    private void showConfirmationSection() {
        confirmationSection.setVisible(true);
        confirmationSection.setManaged(true);
        confirmationPasswordField.clear();
        confirmationPasswordField.requestFocus();
    }

    private void hideConfirmationSection() {
        confirmationSection.setVisible(false);
        confirmationSection.setManaged(false);
        confirmationPasswordField.clear();
        pendingAction = "";
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
