package com.healthtracker.ui;

import com.healthtracker.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MeasurementDetailController {

    @FXML private Label timestampLabel;
    @FXML private VBox measurementsContainer;

    public void setMeasurements(LocalDateTime timestamp, List<Measurement> allMeasurements) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        timestampLabel.setText("Pomiary z dnia: " + timestamp.format(formatter));

        // Znajdź wszystkie pomiary z tego samego timestamp
        List<Measurement> currentMeasurements = allMeasurements.stream()
                .filter(m -> m.getTimestamp().equals(timestamp))
                .toList();

        measurementsContainer.getChildren().clear();

        // Grupuj pomiary według typu - każdy typ tylko raz
        Map<Class<?>, Measurement> measurementsByType = currentMeasurements.stream()
                .collect(Collectors.toMap(
                    Measurement::getClass,
                    m -> m,
                    (existing, replacement) -> existing // W przypadku duplikatów, zachowaj pierwszy
                ));

        // Wyświetl każdy typ pomiaru tylko raz
        measurementsByType.values().forEach(current -> {
            VBox measurementBox = createMeasurementView(current, allMeasurements);
            measurementsContainer.getChildren().add(measurementBox);
        });

        // Jeśli nie ma pomiarów
        if (measurementsByType.isEmpty()) {
            Label noDataLabel = new Label("Brak pomiarów w tym momencie");
            noDataLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-padding: 20;");
            measurementsContainer.getChildren().add(noDataLabel);
        }
    }

    private VBox createMeasurementView(Measurement current, List<Measurement> allMeasurements) {
        VBox box = new VBox(8);
        box.setStyle("-fx-padding: 15; -fx-border-color: #dee2e6; -fx-border-radius: 8; " +
                    "-fx-background-color: #f8f9fa;");

        // Nagłówek typu pomiaru
        Label typeLabel = new Label(getMeasurementTypeName(current));
        typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        // Aktualna wartość
        Label valueLabel = new Label("Aktualna wartość: " + current.getSummary());
        valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

        // Znajdź poprzedni pomiar tego samego typu
        Measurement previous = findPreviousMeasurement(current, allMeasurements);

        Label comparisonLabel = new Label();
        if (previous == null) {
            comparisonLabel.setText("📊 Pierwszy pomiar tego typu");
            comparisonLabel.setStyle("-fx-text-fill: #007bff; -fx-font-style: italic; -fx-font-size: 13px;");
        } else {
            String diff = calculateDifference(current, previous);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String previousDate = previous.getTimestamp().format(dateFormatter);
            
            comparisonLabel.setText("📈 Poprzedni (" + previousDate + "): " + previous.getSummary() + 
                                  " → Zmiana: " + diff);
            comparisonLabel.setStyle("-fx-text-fill: " + getChangeColor(diff) + "; -fx-font-size: 13px;");
        }

        // Dodaj trend jeśli jest poprzedni pomiar
        if (previous != null) {
            String trendText = getTrendAnalysis(current, previous);
            Label trendLabel = new Label(trendText);
            trendLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px; -fx-font-style: italic;");
            box.getChildren().addAll(typeLabel, valueLabel, comparisonLabel, trendLabel);
        } else {
            box.getChildren().addAll(typeLabel, valueLabel, comparisonLabel);
        }

        return box;
    }

    private String getMeasurementTypeName(Measurement measurement) {
        return switch (measurement) {
            case WeightMeasurement wm -> "🏋️ Waga";
            case HeartRateMeasurement hr -> "❤️ Tętno spoczynkowe";
            case BloodPressureMeasurement bp -> "🩺 Ciśnienie krwi";
            default -> "📊 Pomiar";
        };
    }

    private Measurement findPreviousMeasurement(Measurement current, List<Measurement> allMeasurements) {
        return allMeasurements.stream()
                .filter(m -> m.getClass() == current.getClass()) // Ten sam typ
                .filter(m -> m.getTimestamp().isBefore(current.getTimestamp())) // Wcześniejszy
                .max(Comparator.comparing(Measurement::getTimestamp)) // Najnowszy z wcześniejszych
                .orElse(null);
    }

    private String calculateDifference(Measurement current, Measurement previous) {
        if (current instanceof WeightMeasurement c && previous instanceof WeightMeasurement p) {
            double d = c.getWeight() - p.getWeight();
            return String.format("%+.1f kg", d);
        }
        if (current instanceof HeartRateMeasurement c && previous instanceof HeartRateMeasurement p) {
            int d = c.getBpm() - p.getBpm();
            return String.format("%+d BPM", d);
        }
        if (current instanceof BloodPressureMeasurement c && previous instanceof BloodPressureMeasurement p) {
            int ds = c.getSystolic() - p.getSystolic();
            int dd = c.getDiastolic() - p.getDiastolic();
            return String.format("%+d/%+d mmHg", ds, dd);
        }
        return "bez zmian";
    }

    private String getTrendAnalysis(Measurement current, Measurement previous) {
        if (current instanceof WeightMeasurement c && previous instanceof WeightMeasurement p) {
            double diff = c.getWeight() - p.getWeight();
            if (Math.abs(diff) < 0.1) return "💫 Waga stabilna";
            return diff > 0 ? "📈 Trend wzrostowy" : "📉 Trend spadkowy";
        }
        if (current instanceof HeartRateMeasurement c && previous instanceof HeartRateMeasurement p) {
            int diff = c.getBpm() - p.getBpm();
            if (Math.abs(diff) < 3) return "💫 Tętno stabilne";
            return diff > 0 ? "📈 Tętno wyższe" : "📉 Tętno niższe";
        }
        if (current instanceof BloodPressureMeasurement c && previous instanceof BloodPressureMeasurement p) {
            int sysDiff = c.getSystolic() - p.getSystolic();
            int diaDiff = c.getDiastolic() - p.getDiastolic();
            
            if (Math.abs(sysDiff) < 5 && Math.abs(diaDiff) < 3) {
                return "💫 Ciśnienie stabilne";
            }
            
            if (sysDiff > 0 || diaDiff > 0) {
                return "📈 Ciśnienie wyższe";
            } else {
                return "📉 Ciśnienie niższe";
            }
        }
        return "";
    }

    private String getChangeColor(String diff) {
        if (diff.contains("+")) {
            return "#fd7e14"; // Pomarańczowy dla wzrostu (neutralny)
        } else if (diff.contains("-")) {
            return "#20c997"; // Zielonomintowy dla spadku (może być pozytywny)
        } else {
            return "#6c757d"; // Szary dla braku zmian
        }
    }

    @FXML
    private void onClose() {
        ((Stage) timestampLabel.getScene().getWindow()).close();
    }
}
