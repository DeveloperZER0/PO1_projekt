package com.healthtracker.ui.components;

import com.healthtracker.model.Goal;
import com.healthtracker.model.GoalType;
import com.healthtracker.service.GoalService;
import com.healthtracker.service.impl.GoalServiceImpl;
import com.healthtracker.util.SessionManager;
import com.healthtracker.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class GoalFormController {

    @FXML private ComboBox<GoalType> typeCombo;
    @FXML private TextField targetField;
    @FXML private DatePicker dueDatePicker;
    @FXML private TextArea descriptionField;
    @FXML private Button saveButton;
    @FXML private Label unitLabel;

    private final GoalService goalService = new GoalServiceImpl();
    private Goal existingGoal;

    @FXML
    private void initialize() {
        setupGoalTypes();
        
        Goal editedGoal = (Goal) SessionManager.getAttribute("editedGoal");
        if (editedGoal != null) {
            setGoal(editedGoal);
            SessionManager.removeAttribute("editedGoal");
            saveButton.setText("Zaktualizuj cel");
        }
    }

    private void setupGoalTypes() {
        typeCombo.getItems().addAll(GoalType.values());
        typeCombo.setConverter(new StringConverter<GoalType>() {
            @Override
            public String toString(GoalType goalType) {
                return goalType != null ? goalType.getDisplayName() : "";
            }

            @Override
            public GoalType fromString(String string) {
                return null;
            }
        });
        
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                unitLabel.setText("(" + newVal.getUnit() + ")");
                descriptionField.setPromptText(newVal.getDescription());
            }
        });
    }

    public void setGoal(Goal goal) {
        this.existingGoal = goal;
        typeCombo.setValue(goal.getGoalType());
        targetField.setText(String.valueOf(goal.getTargetValue()));
        dueDatePicker.setValue(goal.getDueDate());
        if (goal.getDescription() != null) {
            descriptionField.setText(goal.getDescription());
        }
    }

    @FXML
    private void onSaveClicked() {
        try {
            GoalType selectedType = typeCombo.getValue();
            if (selectedType == null) {
                showError("Wybierz typ celu");
                return;
            }

            String targetText = targetField.getText().trim();
            if (targetText.isEmpty()) {
                showError("Wprowadź wartość docelową");
                return;
            }

            double target = Double.parseDouble(targetText);
            if (target <= 0) {
                showError("Wartość docelowa musi być większa od 0");
                return;
            }

            LocalDate dueDate = dueDatePicker.getValue();
            if (dueDate == null) {
                showError("Wybierz termin realizacji");
                return;
            }

            if (dueDate.isBefore(LocalDate.now())) {
                showError("Termin nie może być w przeszłości");
                return;
            }

            Goal goal;
            boolean isEditing = existingGoal != null;

            if (isEditing) {
                goal = existingGoal;
                goal.setGoalType(selectedType);
                goal.setTargetValue(target);
                goal.setDueDate(dueDate);
                goal.setDescription(descriptionField.getText().trim());
            } else {
                goal = new Goal();
                goal.setUser(SessionManager.getCurrentUser());
                goal.setGoalType(selectedType);
                goal.setTargetValue(target);
                goal.setDueDate(dueDate);
                goal.setDescription(descriptionField.getText().trim());
            }

            if (isEditing) {
                goalService.updateGoal(goal);
                showSuccess("Cel został zaktualizowany!");
            } else {
                goalService.addGoal(goal);
                showSuccess("Cel został dodany!");
            }

            SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");

        } catch (NumberFormatException e) {
            showError("Wprowadź poprawną wartość liczbową");
        } catch (Exception e) {
            showError("Błąd zapisu: " + e.getMessage());
            e.printStackTrace();
        }
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