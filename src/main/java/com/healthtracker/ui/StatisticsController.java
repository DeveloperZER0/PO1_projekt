package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.GoalService;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.impl.ActivityServiceImpl;
import com.healthtracker.service.impl.GoalServiceImpl;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StatisticsController {

    @FXML private LineChart<String, Number> weightChart;
    @FXML private BarChart<String, Number> heartRateChart;
    @FXML private LineChart<String, Number> bpChart;

    @FXML private Label weightStatusLabel;
    @FXML private Label activityStatusLabel;
    @FXML private Label heartRateStatusLabel;
    @FXML private Label bpStatusLabel;

    private final MeasurementService measurementService = new MeasurementServiceImpl();
    private final ActivityService activityService = new ActivityServiceImpl();
    private final GoalService goalService = new GoalServiceImpl();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        List<Measurement> all = measurementService.getMeasurementsByUser(user);
        List<Activity> activities = activityService.getActivitiesByUser(user);
        List<Goal> goals = goalService.getGoalsByUser(user);

        setupCharts(all);
        analyzeHealthStatus(user, all, activities, goals);
    }

    private void setupCharts(List<Measurement> all) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        XYChart.Series<String, Number> weightSeries = new XYChart.Series<>();
        weightSeries.setName("Waga");

        XYChart.Series<String, Number> heartRateSeries = new XYChart.Series<>();
        heartRateSeries.setName("Tętno");

        XYChart.Series<String, Number> systolicSeries = new XYChart.Series<>();
        systolicSeries.setName("Skurczowe");

        XYChart.Series<String, Number> diastolicSeries = new XYChart.Series<>();
        diastolicSeries.setName("Rozkurczowe");

        for (Measurement m : all) {
            String date = m.getTimestamp().format(formatter);
            if (m instanceof WeightMeasurement wm) {
                weightSeries.getData().add(new XYChart.Data<>(date, wm.getWeight()));
            }
            if (m instanceof HeartRateMeasurement hr) {
                heartRateSeries.getData().add(new XYChart.Data<>(date, hr.getBpm()));
            }
            if (m instanceof BloodPressureMeasurement bp) {
                systolicSeries.getData().add(new XYChart.Data<>(date, bp.getSystolic()));
                diastolicSeries.getData().add(new XYChart.Data<>(date, bp.getDiastolic()));
            }
        }

        weightChart.getData().add(weightSeries);
        heartRateChart.getData().add(heartRateSeries);
        bpChart.getData().addAll(systolicSeries, diastolicSeries);
    }

    private void analyzeHealthStatus(User user, List<Measurement> measurements,
                                   List<Activity> activities, List<Goal> goals) {
        analyzeWeight(measurements, goals);
        analyzeActivity(activities, goals);
        analyzeHeartRate(measurements);
        analyzeBloodPressure(measurements);
    }

    private void analyzeWeight(List<Measurement> measurements, List<Goal> goals) {
        Optional<WeightMeasurement> latestWeight = measurements.stream()
            .filter(m -> m instanceof WeightMeasurement)
            .map(m -> (WeightMeasurement) m)
            .max(Comparator.comparing(Measurement::getTimestamp));

        if (latestWeight.isEmpty()) {
            safeSetText(weightStatusLabel, "❓ Brak danych o wadze");
            return;
        }

        double weight = latestWeight.get().getWeight();
        StringBuilder status = new StringBuilder();
        status.append("⚖️ Aktualna waga: ").append(String.format("%.1f kg", weight)).append("\n");

        // Porównanie z celem
        Optional<Goal> weightGoal = goals.stream()
            .filter(g -> g.getGoalType() == GoalType.TARGET_WEIGHT || g.getGoalType() == GoalType.WEIGHT_LOSS)
            .findFirst();

        if (weightGoal.isPresent()) {
            Goal goal = weightGoal.get();
            double target = goal.getTargetValue();
            double diff = weight - target;
            
            if (goal.getGoalType() == GoalType.TARGET_WEIGHT) {
                if (Math.abs(diff) <= 1.0) {
                    status.append("🎯 Cel wagowy osiągnięty! (±1kg)");
                } else if (diff > 0) {
                    status.append("📈 ").append(String.format("%.1f kg", diff)).append(" ponad cel (").append(target).append(" kg)");
                } else {
                    status.append("📉 ").append(String.format("%.1f kg", -diff)).append(" poniżej celu (").append(target).append(" kg)");
                }
            }
        } else {
            // Porównanie z wartościami typowymi (BMI)
            double height = 1.75; // Można dodać pole wzrostu do User
            double bmi = weight / (height * height);
            
            if (bmi < 18.5) {
                status.append("⚠️ Niedowaga (BMI: ").append(String.format("%.1f", bmi)).append(")");
            } else if (bmi < 25) {
                status.append("✅ Waga prawidłowa (BMI: ").append(String.format("%.1f", bmi)).append(")");
            } else if (bmi < 30) {
                status.append("⚠️ Nadwaga (BMI: ").append(String.format("%.1f", bmi)).append(")");
            } else {
                status.append("🚨 Otyłość (BMI: ").append(String.format("%.1f", bmi)).append(")");
            }
        }

        safeSetText(weightStatusLabel, status.toString());
    }

    private void analyzeActivity(List<Activity> activities, List<Goal> goals) {
        // Aktywność w tym tygodniu
        LocalDateTime weekStart = LocalDate.now().atStartOfDay().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        int weeklyMinutes = activities.stream()
            .filter(a -> a.getTimestamp().isAfter(weekStart))
            .mapToInt(Activity::getDurationMinutes)
            .sum();
        
        double weeklyHours = weeklyMinutes / 60.0;
        StringBuilder status = new StringBuilder();
        status.append("🏃 Aktywność w tym tygodniu: ").append(String.format("%.1f h", weeklyHours)).append("\n");

        // Porównanie z celem
        Optional<Goal> activityGoal = goals.stream()
            .filter(g -> g.getGoalType() == GoalType.ACTIVITY_HOURS)
            .findFirst();

        if (activityGoal.isPresent()) {
            double target = activityGoal.get().getTargetValue();
            double progress = (weeklyHours / target) * 100;
            
            if (weeklyHours >= target) {
                status.append("🎯 Cel aktywności osiągnięty! (").append(String.format("%.0f%%", progress)).append(")");
            } else {
                status.append("📊 Postęp: ").append(String.format("%.0f%%", progress)).append(" celu (")
                      .append(target).append(" h)");
            }
        } else {
            // Porównanie z wytycznymi WHO (150 min/tydzień = 2.5h)
            double recommendedHours = 2.5;
            if (weeklyHours >= recommendedHours) {
                status.append("✅ Spełnia wytyczne WHO (≥2.5h/tydzień)");
            } else {
                double missing = recommendedHours - weeklyHours;
                status.append("⚠️ Poniżej wytycznych WHO (brakuje ").append(String.format("%.1f h", missing)).append(")");
            }
        }

        safeSetText(activityStatusLabel, status.toString());
    }

    private void analyzeHeartRate(List<Measurement> measurements) {
        Optional<HeartRateMeasurement> latestHR = measurements.stream()
            .filter(m -> m instanceof HeartRateMeasurement)
            .map(m -> (HeartRateMeasurement) m)
            .max(Comparator.comparing(Measurement::getTimestamp));

        if (latestHR.isEmpty()) {
            safeSetText(heartRateStatusLabel, "❓ Brak danych o tętnie");
            return;
        }

        int bpm = latestHR.get().getBpm();
        StringBuilder status = new StringBuilder();
        status.append("❤️ Ostatnie tętno spoczynkowe: ").append(bpm).append(" BPM\n");

        // Porównanie z wartościami typowymi
        if (bpm < 60) {
            status.append("🔵 Bradykardia (poniżej normy)");
        } else if (bpm <= 100) {
            status.append("✅ Prawidłowe (60-100 BPM)");
        } else {
            status.append("🔴 Tachykardia (powyżej normy)");
        }

        safeSetText(heartRateStatusLabel, status.toString());
    }

    private void analyzeBloodPressure(List<Measurement> measurements) {
        Optional<BloodPressureMeasurement> latestBP = measurements.stream()
            .filter(m -> m instanceof BloodPressureMeasurement)
            .map(m -> (BloodPressureMeasurement) m)
            .max(Comparator.comparing(Measurement::getTimestamp));

        if (latestBP.isEmpty()) {
            safeSetText(bpStatusLabel, "❓ Brak danych o ciśnieniu");
            return;
        }

        BloodPressureMeasurement bp = latestBP.get();
        int systolic = bp.getSystolic();
        int diastolic = bp.getDiastolic();
        
        StringBuilder status = new StringBuilder();
        status.append("🩺 Ostatnie ciśnienie: ").append(systolic).append("/").append(diastolic).append(" mmHg\n");

        // Klasyfikacja według wytycznych ESC/ESH
        if (systolic < 120 && diastolic < 80) {
            status.append("✅ Optymalne");
        } else if (systolic < 130 && diastolic < 85) {
            status.append("✅ Prawidłowe");
        } else if (systolic < 140 && diastolic < 90) {
            status.append("🟡 Wysokie prawidłowe");
        } else if (systolic < 160 && diastolic < 100) {
            status.append("🟠 Nadciśnienie 1° stopnia");
        } else if (systolic < 180 && diastolic < 110) {
            status.append("🔴 Nadciśnienie 2° stopnia");
        } else {
            status.append("🚨 Nadciśnienie 3° stopnia");
        }

        safeSetText(bpStatusLabel, status.toString());
    }

    // Dodaj metodę pomocniczą do bezpiecznego ustawiania tekstu
    private void safeSetText(Label label, String text) {
        if (label != null) {
            label.setText(text);
        } else {
            System.err.println("Warning: Label is null, cannot set text: " + text);
        }
    }
}
