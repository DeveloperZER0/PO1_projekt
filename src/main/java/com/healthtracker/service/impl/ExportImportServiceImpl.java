package com.healthtracker.service.impl;

import com.healthtracker.dao.impl.ActivityTypeDaoImpl;
import com.healthtracker.dao.impl.MealTypeDaoImpl;
import com.healthtracker.model.*;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.ExportImportService;
import com.healthtracker.service.MealService;
import com.healthtracker.service.MeasurementService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExportImportServiceImpl implements ExportImportService {
    
    private final MeasurementService measurementService;
    private final ActivityService activityService;
    private final MealService mealService;
    private final ActivityTypeDaoImpl activityTypeDao;
    private final MealTypeDaoImpl mealTypeDao;

    public ExportImportServiceImpl(MeasurementService measurementService) {
        this.measurementService = measurementService;
        this.activityService = new ActivityServiceImpl();
        this.mealService = new MealServiceImpl();
        this.activityTypeDao = new ActivityTypeDaoImpl();
        this.mealTypeDao = new MealTypeDaoImpl();
    }

    @Override
    public void exportMeasurementsToCsv(User user, File file) throws IOException {
        List<Measurement> measurements = measurementService.getMeasurementsByUser(user);
        
        // Grupuj pomiary według timestamp
        Map<LocalDateTime, List<Measurement>> groupedMeasurements = measurements.stream()
            .collect(Collectors.groupingBy(Measurement::getTimestamp));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            // Nagłówek
            writer.println("ID,Timestamp,Type,BloodPressure,HeartRate,Weight,Summary");

            // Dane - każdy pomiar w osobnej linii
            for (Measurement measurement : measurements) {
                StringBuilder line = new StringBuilder();
                line.append(measurement.getId()).append(",");
                line.append(measurement.getTimestamp().format(formatter)).append(",");
                line.append(measurement.getClass().getSimpleName()).append(",");
                
                // Ciśnienie
                if (measurement instanceof BloodPressureMeasurement bp) {
                    line.append(bp.getSystolic()).append("/").append(bp.getDiastolic()).append(" mmHg");
                } else {
                    line.append("-");
                }
                line.append(",");
                
                // Tętno
                if (measurement instanceof HeartRateMeasurement hr) {
                    line.append(hr.getBpm()).append(" bpm");
                } else {
                    line.append("-");
                }
                line.append(",");
                
                // Waga
                if (measurement instanceof WeightMeasurement w) {
                    line.append(w.getWeight()).append(" kg");
                } else {
                    line.append("-");
                }
                line.append(",");
                
                // Summary
                String summary = measurement.getSummary() != null ? measurement.getSummary() : "";
                if (summary.contains(",") || summary.contains("\"")) {
                    summary = "\"" + summary.replace("\"", "\"\"") + "\"";
                }
                line.append(summary);
                
                writer.println(line.toString());
            }
        }
    }

    @Override
    public void importMeasurementsFromCsv(User user, File file) throws IOException {
        List<Measurement> existingMeasurements = measurementService.getMeasurementsByUser(user);
        Set<String> existingKeys = new HashSet<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Measurement m : existingMeasurements) {
            String key = m.getTimestamp().format(formatter) + ":" + m.getClass().getSimpleName();
            existingKeys.add(key);
        }

        int importedCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Pomijamy nagłówek
                }
                
                String[] parts = line.split(",");
                if (parts.length < 6) continue; // Potrzebujemy co najmniej 6 kolumn

                try {
                    String timestampStr = parts[1].trim();
                    String type = parts[2].trim();
                    
                    // Sprawdź duplikaty
                    String key = timestampStr + ":" + type;
                    if (existingKeys.contains(key)) {
                        System.out.println("Duplikat pominięty: " + key);
                        continue;
                    }

                    Measurement m = null;
                    LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

                    switch (type) {
                        case "WeightMeasurement" -> {
                            if (parts.length > 5 && !parts[5].trim().isEmpty() && !parts[5].trim().equals("-")) {
                                String weightStr = parts[5].replace(" kg", "").trim();
                                double weight = Double.parseDouble(weightStr);
                                WeightMeasurement wm = new WeightMeasurement();
                                wm.setWeight(weight);
                                wm.setUser(user);
                                wm.setTimestamp(timestamp);
                                // if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                                //     wm.setSummary(parts[6].trim());
                                // }
                                m = wm;
                            }
                        }
                        case "BloodPressureMeasurement" -> {
                            if (parts.length > 3 && !parts[3].trim().isEmpty() && !parts[3].trim().equals("-")) {
                                String bpValue = parts[3].replace(" mmHg", "").replace("mmHg", "").trim();
                                if (bpValue.contains("/")) {
                                    String[] bp = bpValue.split("/");
                                    int sys = Integer.parseInt(bp[0].trim());
                                    int dia = Integer.parseInt(bp[1].trim());
                                    BloodPressureMeasurement bpm = new BloodPressureMeasurement();
                                    bpm.setSystolic(sys);
                                    bpm.setDiastolic(dia);
                                    bpm.setUser(user);
                                    bpm.setTimestamp(timestamp);
                                    // if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                                    //     bpm.setSummary(parts[6].trim());
                                    // }
                                    m = bpm;
                                }
                            }
                        }
                        case "HeartRateMeasurement" -> {
                            if (parts.length > 4 && !parts[4].trim().isEmpty() && !parts[4].trim().equals("-")) {
                                String hrValue = parts[4].replace(" bpm", "").replace("bpm", "").trim();
                                int bpm = Integer.parseInt(hrValue);
                                HeartRateMeasurement hrm = new HeartRateMeasurement();
                                hrm.setBpm(bpm);
                                hrm.setUser(user);
                                hrm.setTimestamp(timestamp);
                                // if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                                //     hrm.setSummary(parts[6].trim());
                                // }
                                m = hrm;
                            }
                        }
                    }

                    if (m != null) {
                        measurementService.addMeasurement(m);
                        importedCount++;
                        System.out.println("Zaimportowano: " + type + " z " + timestampStr);
                    }

                } catch (Exception e) {
                    System.err.println("Nieprawidłowy wiersz: " + line);
                    System.err.println("Błąd: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Import zakończony. Zaimportowano " + importedCount + " pomiarów.");
    }

    @Override
    public void exportActivitiesToCsv(User user, File file) throws IOException {
        List<Activity> activities = activityService.getActivitiesByUser(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (PrintWriter writer = new PrintWriter(file)) {
            // Nagłówek
            writer.println("ID,Timestamp,Type,Duration(min),Distance(km),Calories,HeartRateAvg,HeartRateMax,Intensity,Notes");

            // Dane
            for (Activity activity : activities) {
                StringBuilder line = new StringBuilder();
                line.append(activity.getId()).append(",");
                line.append(activity.getTimestamp().format(formatter)).append(",");
                line.append(activity.getType().getName()).append(",");
                line.append(activity.getDurationMinutes()).append(",");
                line.append(activity.getDistanceKm() != null ? activity.getDistanceKm() : "").append(",");
                line.append(activity.getCaloriesBurned() != null ? activity.getCaloriesBurned() : "").append(",");
                line.append(activity.getHeartRateAvg() != null ? activity.getHeartRateAvg() : "").append(",");
                line.append(activity.getHeartRateMax() != null ? activity.getHeartRateMax() : "").append(",");
                line.append(activity.getIntensity() != null ? activity.getIntensity().name() : "").append(",");
                line.append(activity.getNotes() != null ? "\"" + activity.getNotes().replace("\"", "\"\"") + "\"" : "");
                
                writer.println(line.toString());
            }
        }
    }

    @Override
    public void importActivitiesFromCsv(User user, File file) throws IOException {
        int importedCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Pomijamy nagłówek
                }
                
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                try {
                    String timestampStr = parts[1].trim();
                    String typeName = parts[2].trim();
                    int duration = Integer.parseInt(parts[3].trim());
                    
                    // Znajdź lub stwórz ActivityType
                    ActivityType activityType = activityTypeDao.findByName(typeName);
                    if (activityType == null) {
                        activityType = new ActivityType(typeName, "min", false, ActivityCategory.CARDIO);
                        activityTypeDao.save(activityType);
                    }

                    Activity activity = new Activity();
                    activity.setUser(user);
                    activity.setType(activityType);
                    activity.setDurationMinutes(duration);
                    activity.setTimestamp(LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    
                    // Opcjonalne pola
                    if (parts.length > 4 && !parts[4].trim().isEmpty() && !parts[4].trim().equals("-")) {
                        activity.setDistanceKm(Double.parseDouble(parts[4].trim()));
                    }
                    if (parts.length > 5 && !parts[5].trim().isEmpty() && !parts[5].trim().equals("-")) {
                        activity.setCaloriesBurned(Integer.parseInt(parts[5].trim()));
                    }
                    if (parts.length > 6 && !parts[6].trim().isEmpty() && !parts[6].trim().equals("-")) {
                        activity.setHeartRateAvg(Integer.parseInt(parts[6].trim()));
                    }
                    if (parts.length > 7 && !parts[7].trim().isEmpty() && !parts[7].trim().equals("-")) {
                        activity.setHeartRateMax(Integer.parseInt(parts[7].trim()));
                    }
                    if (parts.length > 8 && !parts[8].trim().isEmpty() && !parts[8].trim().equals("-")) {
                        try {
                            activity.setIntensity(IntensityLevel.valueOf(parts[8].trim()));
                        } catch (IllegalArgumentException e) {
                            // Ignoruj nieprawidłowe wartości intensity
                        }
                    }
                    if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                        String notes = parts[9].trim();
                        if (notes.startsWith("\"") && notes.endsWith("\"")) {
                            notes = notes.substring(1, notes.length() - 1).replace("\"\"", "\"");
                        }
                        activity.setNotes(notes);
                    }

                    activityService.addActivity(activity);
                    importedCount++;
                    System.out.println("Zaimportowano aktywność: " + typeName + " z " + timestampStr);

                } catch (Exception e) {
                    System.err.println("Nieprawidłowy wiersz aktywności: " + line);
                    System.err.println("Błąd: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Import aktywności zakończony. Zaimportowano " + importedCount + " aktywności.");
    }

    @Override
    public void exportMealsToCsv(User user, File file) throws IOException {
        List<Meal> meals = mealService.getMealsByUser(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (PrintWriter writer = new PrintWriter(file)) {
            // Nagłówek
            writer.println("ID,Timestamp,Type,Calories,Description");

            // Dane
            for (Meal meal : meals) {
                StringBuilder line = new StringBuilder();
                line.append(meal.getId()).append(",");
                line.append(meal.getTimestamp().format(formatter)).append(",");
                line.append(meal.getType().getName()).append(",");
                line.append(meal.getCalories()).append(",");
                line.append("\"").append(meal.getDescription().replace("\"", "\"\"")).append("\"");
                
                writer.println(line.toString());
            }
        }
    }

    @Override
    public void importMealsFromCsv(User user, File file) throws IOException {
        int importedCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Pomijamy nagłówek
                }
                
                String[] parts = line.split(",", 5); // Maksymalnie 5 części
                if (parts.length < 4) continue;

                try {
                    String timestampStr = parts[1].trim();
                    String typeName = parts[2].trim();
                    int calories = Integer.parseInt(parts[3].trim());
                    String description = parts.length > 4 ? parts[4].trim() : "";
                    
                    // Usuń cudzysłowy z opisu
                    if (description.startsWith("\"") && description.endsWith("\"")) {
                        description = description.substring(1, description.length() - 1).replace("\"\"", "\"");
                    }
                    
                    // Znajdź lub stwórz MealType
                    MealType mealType = mealTypeDao.findByName(typeName);
                    if (mealType == null) {
                        mealType = new MealType();
                        mealType.setName(typeName);
                        mealTypeDao.save(mealType);
                    }

                    Meal meal = new Meal();
                    meal.setUser(user);
                    meal.setType(mealType);
                    meal.setCalories(calories);
                    meal.setDescription(description);
                    meal.setTimestamp(LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                    mealService.addMeal(meal);
                    importedCount++;
                    System.out.println("Zaimportowano posiłek: " + typeName + " z " + timestampStr);

                } catch (Exception e) {
                    System.err.println("Nieprawidłowy wiersz posiłku: " + line);
                    System.err.println("Błąd: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Import posiłków zakończony. Zaimportowano " + importedCount + " posiłków.");
    }
}