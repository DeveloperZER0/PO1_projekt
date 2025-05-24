package com.healthtracker.ui;

import com.healthtracker.model.*;
import com.healthtracker.service.MeasurementService;
import com.healthtracker.service.impl.MeasurementServiceImpl;
import com.healthtracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatisticsController {

    @FXML private LineChart<String, Number> weightChart;
    @FXML private BarChart<String, Number> heartRateChart;
    @FXML private LineChart<String, Number> bpChart;

    private final MeasurementService measurementService = new MeasurementServiceImpl();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        List<Measurement> all = measurementService.getMeasurementsByUser(user);

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
}
