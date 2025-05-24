package com.healthtracker.ui;

import com.healthtracker.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;

public class MeasurementDetailController {

    @FXML private Label timestampLabel;
    @FXML private Label typeLabel;
    @FXML private Label valueLabel;
    @FXML private Label comparisonLabel;

    public void setMeasurement(Measurement current, List<Measurement> history) {
        timestampLabel.setText("Data: " + current.getTimestamp().toString());
        typeLabel.setText("Typ: " + current.getClass().getSimpleName());
        valueLabel.setText("Wartość: " + current.getSummary());

        Measurement previous = history.stream()
                .filter(m -> m.getClass() == current.getClass() && m.getTimestamp().isBefore(current.getTimestamp()))
                .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .orElse(null);

        if (previous == null) {
            comparisonLabel.setText("Brak wcześniejszego pomiaru do porównania.");
        } else {
            String diff = calculateDifference(current, previous);
            comparisonLabel.setText("Poprzedni: " + previous.getSummary() + " → Zmiana: " + diff);
        }
    }

    private String calculateDifference(Measurement current, Measurement previous) {
        if (current instanceof WeightMeasurement c && previous instanceof WeightMeasurement p) {
            double d = c.getWeight() - p.getWeight();
            return String.format("%.1f kg", d);
        }
        if (current instanceof HeartRateMeasurement c && previous instanceof HeartRateMeasurement p) {
            int d = c.getBpm() - p.getBpm();
            return d + " BPM";
        }
        if (current instanceof BloodPressureMeasurement c && previous instanceof BloodPressureMeasurement p) {
            int ds = c.getSystolic() - p.getSystolic();
            int dd = c.getDiastolic() - p.getDiastolic();
            return ds + "/" + dd + " mmHg";
        }
        return "(brak różnicy)";
    }

    @FXML
    private void onClose() {
        ((Stage) timestampLabel.getScene().getWindow()).close();
    }
}
