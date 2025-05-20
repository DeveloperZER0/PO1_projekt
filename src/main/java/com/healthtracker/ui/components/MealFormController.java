package com.healthtracker.ui.components;

import com.healthtracker.model.Meal;
import com.healthtracker.model.MealType;
import com.healthtracker.model.User;
import com.healthtracker.service.MealService;
import com.healthtracker.service.impl.MealServiceImpl;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

/**
 * Kontroler formularza dodawania posiłku.
 */
public class MealFormController {

    @FXML
    private TextField caloriesField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private Button saveButton;

    private final MealService mealService = new MealServiceImpl();

    @FXML
    private void initialize() {
        typeCombo.getItems().addAll("Śniadanie", "Obiad", "Kolacja", "Przekąska");
    }

    @FXML
    private void onSaveClicked() {
        try {
            int calories = Integer.parseInt(caloriesField.getText());
            String description = descriptionField.getText();
            String typeName = typeCombo.getValue();

            MealType type = new MealType();
            type.setName(typeName);

            Meal meal = new Meal();
            meal.setUser(SessionManager.getCurrentUser());
            meal.setCalories(calories);
            meal.setDescription(description);
            meal.setType(type);
            meal.setTimestamp(LocalDateTime.now());

            mealService.addMeal(meal);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Posiłek zapisany!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Błąd: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
