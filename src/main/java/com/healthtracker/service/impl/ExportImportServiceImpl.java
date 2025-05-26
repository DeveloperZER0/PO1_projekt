package com.healthtracker.service.impl;

import com.healthtracker.dao.impl.ActivityTypeDaoImpl;
import com.healthtracker.dao.impl.MealTypeDaoImpl;
import com.healthtracker.model.*;
import com.healthtracker.service.ActivityService;
import com.healthtracker.service.ExportImportService;
import com.healthtracker.service.MealService;
import com.healthtracker.service.MeasurementService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("ID,Data,Typ,Opis\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Measurement m : measurements) {
                writer.write(String.format("%d,%s,%s,%s\n",
                        m.getId(),
                        m.getTimestamp().format(formatter),
                        m.getClass().getSimpleName(),
                        m.getSummary()
                ));
            }
        }
    }

    @Override
    public void importMeasurementsFromCsv(User user, File file) throws IOException {
        List<Measurement> existingMeasurements = measurementService.getMeasurementsByUser(user);
        Set<String> existingKeys = new HashSet<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Measurement m : existingMeasurements) {
            String key = m.getTimestamp().format(formatter) + ":" + m.getClass().getSimpleName() + ":" + m.getSummary();
            existingKeys.add(key);
        }

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

                String timestampStr = parts[1];
                String type = parts[2];
                String summary = parts[3];
                String key = timestampStr + ":" + type + ":" + summary;

                if (existingKeys.contains(key)) {
                    System.out.println("Duplikat pominięty: " + key);
                    continue;
                }

                try {
                    Measurement m = null;
                    switch (type) {
                        case "WeightMeasurement" -> {
                            double weight = Double.parseDouble(summary.replace(" kg", ""));
                            WeightMeasurement wm = new WeightMeasurement();
                            wm.setWeight(weight);
                            m = wm;
                        }
                        case "BloodPressureMeasurement" -> {
                            String[] bp = summary.replace(" mmHg", "").split("/");
                            int sys = Integer.parseInt(bp[0]);
                            int dia = Integer.parseInt(bp[1]);
                            BloodPressureMeasurement bpm = new BloodPressureMeasurement();
                            bpm.setSystolic(sys);
                            bpm.setDiastolic(dia);
                            m = bpm;
                        }
                        case "HeartRateMeasurement" -> {
                            int bpm = Integer.parseInt(summary.replace(" BPM", ""));
                            HeartRateMeasurement hrm = new HeartRateMeasurement();
                            hrm.setBpm(bpm);
                            m = hrm;
                        }
                    }

                    if (m != null) {
                        m.setUser(user);
                        m.setTimestamp(LocalDateTime.parse(timestampStr, formatter));
                        measurementService.addMeasurement(m);
                    }

                } catch (Exception e) {
                    System.err.println("Nieprawidłowy wiersz: " + line);
                }
            }
        }
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
                    String timestampStr = parts[1];
                    String typeName = parts[2];
                    int duration = Integer.parseInt(parts[3]);
                    
                    // Znajdź lub stwórz ActivityType
                    ActivityType activityType = activityTypeDao.findByName(typeName);
                    if (activityType == null) {
                        // Stwórz podstawowy typ aktywności
                        activityType = new ActivityType(typeName, "min", false, ActivityCategory.CARDIO);
                        activityTypeDao.save(activityType);
                    }

                    Activity activity = new Activity();
                    activity.setUser(user);
                    activity.setType(activityType);
                    activity.setDurationMinutes(duration);
                    activity.setTimestamp(LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    
                    // Opcjonalne pola
                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        activity.setDistanceKm(Double.parseDouble(parts[4]));
                    }
                    if (parts.length > 5 && !parts[5].isEmpty()) {
                        activity.setCaloriesBurned(Integer.parseInt(parts[5]));
                    }
                    if (parts.length > 6 && !parts[6].isEmpty()) {
                        activity.setHeartRateAvg(Integer.parseInt(parts[6]));
                    }
                    if (parts.length > 7 && !parts[7].isEmpty()) {
                        activity.setHeartRateMax(Integer.parseInt(parts[7]));
                    }
                    if (parts.length > 8 && !parts[8].isEmpty()) {
                        activity.setIntensity(IntensityLevel.valueOf(parts[8]));
                    }
                    if (parts.length > 9 && !parts[9].isEmpty()) {
                        String notes = parts[9];
                        if (notes.startsWith("\"") && notes.endsWith("\"")) {
                            notes = notes.substring(1, notes.length() - 1).replace("\"\"", "\"");
                        }
                        activity.setNotes(notes);
                    }

                    activityService.addActivity(activity);

                } catch (Exception e) {
                    System.err.println("Nieprawidłowy wiersz aktywności: " + line);
                    e.printStackTrace();
                }
            }
        }
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
                    String timestampStr = parts[1];
                    String typeName = parts[2];
                    int calories = Integer.parseInt(parts[3]);
                    String description = parts.length > 4 ? parts[4] : "";
                    
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

                } catch (Exception e) {
                    System.err.println("Nieprawidłowy wiersz posiłku: " + line);
                    e.printStackTrace();
                }
            }
        }
    }
}