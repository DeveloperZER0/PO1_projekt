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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfileController {

    @FXML private Label usernameLabel, emailLabel, roleLabel;

    @FXML private TableView<Goal> goalTable;
    @FXML private TableColumn<Goal, String> goalTypeColumn;
    @FXML private TableColumn<Goal, String> goalTargetColumn;
    @FXML private TableColumn<Goal, String> goalDueColumn;

    @FXML private LineChart<String, Number> weightChart;

    private final GoalService goalService = new GoalServiceImpl();
    private final MeasurementService measurementService = new MeasurementServiceImpl();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole().toString());

        setupGoals(user);
        setupWeightChart(user);
    }

    private void setupGoals(User user) {
        goalTypeColumn.setCellValueFactory(g -> new SimpleStringProperty(g.getValue().getGoalType().name()));
        goalTargetColumn.setCellValueFactory(g -> new SimpleStringProperty(String.valueOf(g.getValue().getTargetValue())));
        goalDueColumn.setCellValueFactory(g -> new SimpleStringProperty(g.getValue().getDueDate().toString()));
        goalTable.setItems(FXCollections.observableArrayList(goalService.getGoalsByUser(user)));
    }

    private void setupWeightChart(User user) {
        List<Measurement> measurements = measurementService.getMeasurementsByUser(user);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Waga");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (Measurement m : measurements) {
            if (m instanceof WeightMeasurement wm) {
                String label = wm.getTimestamp().format(formatter);
                series.getData().add(new XYChart.Data<>(label, wm.getWeight()));
            }
        }

        weightChart.getData().clear();
        weightChart.getData().add(series);
    }
}
