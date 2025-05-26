package com.healthtracker.service;

import com.healthtracker.model.Activity;
import com.healthtracker.model.Meal;
import com.healthtracker.model.User;

import java.io.File;
import java.io.IOException;

public interface ExportImportService {
    
    // Measurements
    void exportMeasurementsToCsv(User user, File file) throws IOException;
    void importMeasurementsFromCsv(User user, File file) throws IOException;
    
    // Activities
    void exportActivitiesToCsv(User user, File file) throws IOException;
    void importActivitiesFromCsv(User user, File file) throws IOException;
    
    // Meals
    void exportMealsToCsv(User user, File file) throws IOException;
    void importMealsFromCsv(User user, File file) throws IOException;
}
