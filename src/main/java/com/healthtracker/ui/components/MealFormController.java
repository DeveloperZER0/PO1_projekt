package com.healthtracker.ui.components;

import com.healthtracker.model.Meal;
import com.healthtracker.model.MealType;
import com.healthtracker.service.MealService;
import com.healthtracker.service.impl.MealServiceImpl;
import com.healthtracker.util.SessionManager;
import com.healthtracker.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

/**
 * Kontroler formularza dodawania posiłku.
 */
public class MealFormController {

    @FXML private TextField caloriesField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private Button saveButton;

    private final MealService mealService = new MealServiceImpl();

    @FXML
    private void initialize() {
        typeCombo.getItems().addAll("Śniadanie", "Obiad", "Kolacja", "Przekąska");
    }

    @FXML
    private void onSaveClicked() {
        try {
            int calories = Integer.parseInt(caloriesField.getText().trim());
            String description = descriptionField.getText().trim();
            String typeName = typeCombo.getValue();

            if (typeName == null || typeName.isEmpty()) {
                showError("Wybierz typ posiłku");
                return;
            }

            if (calories <= 0) {
                showError("Kalorie muszą być większe od 0");
                return;
            }

            if (description.isEmpty()) {
                showError("Wprowadź opis posiłku");
                return;
            }

            MealType type = new MealType();
            type.setName(typeName);

            Meal meal = new Meal();
            meal.setUser(SessionManager.getCurrentUser());
            meal.setCalories(calories);
            meal.setDescription(description);
            meal.setType(type);
            meal.setTimestamp(LocalDateTime.now());

            mealService.addMeal(meal);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Posiłek został zapisany!");
            alert.showAndWait();
            
            SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");

        } catch (NumberFormatException e) {
            showError("Wprowadź poprawną liczbę kalorii");
        } catch (Exception e) {
            showError("Błąd zapisu: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelClicked() {
        SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
