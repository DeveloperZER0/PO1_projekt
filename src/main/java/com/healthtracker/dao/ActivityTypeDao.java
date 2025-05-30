package com.healthtracker.dao;

import com.healthtracker.model.ActivityType;

import java.util.List;

public interface ActivityTypeDao extends GenericDao<ActivityType> {
    ActivityType findByName(String name);
    List<ActivityType> findByRequiresDistance(boolean requiresDistance);
}