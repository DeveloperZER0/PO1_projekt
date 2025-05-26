package com.healthtracker.service.impl;

import com.healthtracker.dao.ActivityDao;
import com.healthtracker.dao.impl.ActivityDaoImpl;
import com.healthtracker.model.Activity;
import com.healthtracker.model.User;
import com.healthtracker.service.ActivityService;

import java.time.LocalDateTime;
import java.util.List;

public class ActivityServiceImpl implements ActivityService {
    private final ActivityDao activityDao = new ActivityDaoImpl();
    @Override
    public void addActivity(Activity activity) {
        activityDao.save(activity);
    }

    @Override
    public List<Activity> getActivitiesByUser(User user) {
        return activityDao.findByUser(user);
    }

    @Override
    public void deleteActivity(Activity activity) {
        activityDao.delete(activity);
    }

    @Override
    public Activity getById(Long id) {
        return activityDao.findById(id);
    }

    @Override
    public void updateActivity(Activity activity) {
        activityDao.update(activity);
    }

}
