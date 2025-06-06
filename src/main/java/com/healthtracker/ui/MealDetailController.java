package com.healthtracker.ui;

import com.healthtracker.model.Meal;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class MealDetailController {

    @FXML private Label mealTitleLabel;
    @FXML private Label typeLabel;
    @FXML private Label caloriesLabel;
    @FXML private Label timestampLabel;
    @FXML private Label descriptionLabel;

    public void setMeal(Meal meal) {
        mealTitleLabel.setText("🍽️ " + meal.getType().getName());
        
        typeLabel.setText("Typ posiłku: " + meal.getType().getName());
        caloriesLabel.setText("Kalorie: " + meal.getCalories() + " kcal");
        timestampLabel.setText("Data: " + meal.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        
        if (meal.getDescription() != null && !meal.getDescription().trim().isEmpty()) {
            descriptionLabel.setText(meal.getDescription());
            descriptionLabel.setVisible(true);
        } else {
            descriptionLabel.setText("Brak opisu");
            descriptionLabel.setVisible(true);
        }
    }

    @FXML
    private void onClose() {
        ((Stage) mealTitleLabel.getScene().getWindow()).close();
    }
}