package com.healthtracker.ui;

import com.healthtracker.model.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class ActivityDetailController {

    @FXML private Label activityTitleLabel;
    @FXML private Label typeLabel;
    @FXML private Label durationLabel;
    @FXML private Label distanceLabel;
    @FXML private Label caloriesLabel;
    @FXML private Label heartRateLabel;
    @FXML private Label intensityLabel;
    @FXML private Label timestampLabel;
    @FXML private Label notesLabel;

    public void setActivity(Activity activity) {
        activityTitleLabel.setText(activity.getType().getCategory().getEmoji() + " " + activity.getType().getName());
        
        typeLabel.setText("Typ: " + activity.getType().getName());
        durationLabel.setText("Czas trwania: " + activity.getDurationMinutes() + " min");
        
        if (activity.getDistanceKm() != null && activity.getDistanceKm() > 0) {
            if ("m".equals(activity.getType().getUnit())) {
                distanceLabel.setText("Dystans: " + String.format("%.0f m", activity.getDistanceKm() * 1000));
            } else {
                distanceLabel.setText("Dystans: " + String.format("%.2f km", activity.getDistanceKm()));
            }
            distanceLabel.setVisible(true);
        } else {
            distanceLabel.setVisible(false);
        }
        
        if (activity.getCaloriesBurned() != null) {
            caloriesLabel.setText("Spalone kalorie: " + activity.getCaloriesBurned() + " kcal");
            caloriesLabel.setVisible(true);
        } else {
            caloriesLabel.setVisible(false);
        }
        
        if (activity.getHeartRateAvg() != null || activity.getHeartRateMax() != null) {
            StringBuilder hrText = new StringBuilder("Tętno: ");
            if (activity.getHeartRateAvg() != null) {
                hrText.append("śr. ").append(activity.getHeartRateAvg()).append(" bpm");
            }
            if (activity.getHeartRateMax() != null) {
                if (activity.getHeartRateAvg() != null) {
                    hrText.append(", ");
                }
                hrText.append("maks. ").append(activity.getHeartRateMax()).append(" bpm");
            }
            heartRateLabel.setText(hrText.toString());
            heartRateLabel.setVisible(true);
        } else {
            heartRateLabel.setVisible(false);
        }
        
        if (activity.getIntensity() != null) {
            intensityLabel.setText("Intensywność: " + activity.getIntensity().getEmoji() + " " + activity.getIntensity().getDisplayName());
            intensityLabel.setVisible(true);
        } else {
            intensityLabel.setVisible(false);
        }
        
        timestampLabel.setText("Data: " + activity.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        
        if (activity.getNotes() != null && !activity.getNotes().trim().isEmpty()) {
            notesLabel.setText("Notatki: " + activity.getNotes());
            notesLabel.setVisible(true);
        } else {
            notesLabel.setVisible(false);
        }
    }

    @FXML
    private void onClose() {
        ((Stage) activityTitleLabel.getScene().getWindow()).close();
    }
}