package com.healthtracker.ui.components;

import com.healthtracker.dao.impl.MealTypeDaoImpl;
import com.healthtracker.model.Meal;
import com.healthtracker.model.MealType;
import com.healthtracker.service.MealService;
import com.healthtracker.service.impl.MealServiceImpl;
import com.healthtracker.util.SessionManager;
import com.healthtracker.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDateTime;

/**
 * Kontroler formularza dodawania posiłku.
 */
public class MealFormController {

    @FXML private TextField caloriesField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<MealType> typeCombo;
    @FXML private Button saveButton;

    private final MealService mealService = new MealServiceImpl();
    private final MealTypeDaoImpl mealTypeDao = new MealTypeDaoImpl();
    private Meal existingMeal; // Dodaj to pole

    @FXML
    private void initialize() {
        // Inicjalizuj domyślne typy jeśli baza jest pusta
        if (mealTypeDao.findAll().isEmpty()) {
            initializeDefaultMealTypes();
        }
        
        // Załaduj typy z bazy
        typeCombo.getItems().addAll(mealTypeDao.findAll());
        typeCombo.setConverter(new StringConverter<MealType>() {
            @Override
            public String toString(MealType mealType) {
                return mealType != null ? mealType.getName() : "";
            }

            @Override
            public MealType fromString(String string) {
                return null;
            }
        });
        
        // Sprawdź czy edytujemy istniejący posiłek
        Meal editedMeal = (Meal) SessionManager.getAttribute("editedMeal");
        if (editedMeal != null) {
            setMeal(editedMeal);
            SessionManager.removeAttribute("editedMeal");
            saveButton.setText("Zaktualizuj posiłek");
        }
    }

    public void setMeal(Meal meal) {
        this.existingMeal = meal;
        
        // Wypełnij pola danymi z posiłku
        typeCombo.setValue(meal.getType());
        caloriesField.setText(String.valueOf(meal.getCalories()));
        descriptionField.setText(meal.getDescription());
    }

    @FXML
    private void onSaveClicked() {
        try {
            int calories = Integer.parseInt(caloriesField.getText().trim());
            String description = descriptionField.getText().trim();
            MealType selectedType = typeCombo.getValue();

            if (selectedType == null) {
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

            Meal meal;
            boolean isEditing = existingMeal != null;

            if (isEditing) {
                // Tryb edycji - zaktualizuj istniejący posiłek
                meal = existingMeal;
                meal.setType(selectedType);
                meal.setCalories(calories);
                meal.setDescription(description);
                // Nie zmieniamy timestamp przy edycji
            } else {
                // Tryb dodawania - stwórz nowy posiłek
                meal = new Meal();
                meal.setUser(SessionManager.getCurrentUser());
                meal.setType(selectedType);
                meal.setCalories(calories);
                meal.setDescription(description);
                meal.setTimestamp(LocalDateTime.now());
            }

            if (isEditing) {
                mealService.updateMeal(meal);
                showSuccess("Posiłek został zaktualizowany!");
            } else {
                mealService.addMeal(meal);
                showSuccess("Posiłek został zapisany!");
            }
            
            SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");

        } catch (NumberFormatException e) {
            showError("Wprowadź poprawną liczbę kalorii");
        } catch (Exception e) {
            showError("Błąd zapisu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Sukces");
        alert.showAndWait();
    }

    private void initializeDefaultMealTypes() {
        createAndSaveMealType("Śniadanie");
        createAndSaveMealType("Obiad");
        createAndSaveMealType("Kolacja");
        createAndSaveMealType("Przekąska");
        createAndSaveMealType("Drugie śniadanie");
        createAndSaveMealType("Podwieczorek");
    }

    private void createAndSaveMealType(String name) {
        MealType type = new MealType();
        type.setName(name);
        mealTypeDao.save(type);
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
