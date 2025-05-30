package com.healthtracker.service.impl;

import com.healthtracker.dao.impl.ActivityTypeDaoImpl;
import com.healthtracker.model.ActivityCategory;
import com.healthtracker.model.ActivityType;

import java.util.List;

public class ActivityTypeInitService {
    
    private final ActivityTypeDaoImpl activityTypeDao = new ActivityTypeDaoImpl();
    
    public void initializeDefaultActivityTypes() {
        if (activityTypeDao.findAll().isEmpty()) {
            List<ActivityType> defaultTypes = List.of(
                new ActivityType("Bieganie", "km", true, ActivityCategory.CARDIO),
                new ActivityType("Rower", "km", true, ActivityCategory.CARDIO),
                new ActivityType("Chodzenie", "km", true, ActivityCategory.CARDIO),
                new ActivityType("Pływanie", "m", true, ActivityCategory.WATER_SPORTS),
                new ActivityType("Siłownia", "serie", false, ActivityCategory.STRENGTH),
                new ActivityType("Joga", "min", false, ActivityCategory.FLEXIBILITY),
                new ActivityType("Aerobik", "min", false, ActivityCategory.CARDIO),
                new ActivityType("Pilates", "min", false, ActivityCategory.FLEXIBILITY),
                new ActivityType("Tenis", "sety", false, ActivityCategory.TEAM_SPORTS),
                new ActivityType("Koszykówka", "min", false, ActivityCategory.TEAM_SPORTS),
                new ActivityType("Wspinaczka", "m", true, ActivityCategory.OUTDOOR)
            );
            
            for (ActivityType type : defaultTypes) {
                type.setDescription("Domyślny typ aktywności: " + type.getName());
                activityTypeDao.save(type);
            }
        }
    }
}