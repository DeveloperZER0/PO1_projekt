package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.GoalService;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.impl.GoalServiceImpl;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProfileController {

    @FXML private Label usernameLabel, emailLabel, roleLabel;

    @FXML private TableView<Goal> goalTable;
    @FXML private TableColumn<Goal, String> goalTypeColumn;
    @FXML private TableColumn<Goal, String> goalTargetColumn;
    @FXML private TableColumn<Goal, String> goalDueColumn;

    private final GoalService goalService = new GoalServiceImpl();
    private final MeasurementService measurementService = new MeasurementServiceImpl();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        setupUserInfo(user);
        setupGoals(user);
        // Usuń setupChart(user); - nie potrzebujemy już wykresu
    }

    private void setupUserInfo(User user) {
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole().toString());
    }

    private void setupGoals(User user) {
        goalTypeColumn.setCellValueFactory(g -> {
            GoalType goalType = (GoalType) g.getValue().getGoalType();
            return new SimpleStringProperty(goalType != null ? goalType.name() : "");
        });
        goalTargetColumn.setCellValueFactory(g -> 
            new SimpleStringProperty(String.valueOf(g.getValue().getTargetValue())));
        goalDueColumn.setCellValueFactory(g -> 
            new SimpleStringProperty(g.getValue().getDueDate().toString()));
        
        try {
            goalTable.setItems(FXCollections.observableArrayList(goalService.getGoalsByUser(user)));
        } catch (Exception e) {
            // Jeśli nie ma celów lub błąd - ustaw pustą listę
            goalTable.setItems(FXCollections.observableArrayList());
            System.err.println("Błąd ładowania celów: " + e.getMessage());
        }
    }
}
