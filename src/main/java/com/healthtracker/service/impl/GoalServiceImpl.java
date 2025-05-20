package com.healthtracker.service.impl;

import com.healthtracker.dao.GoalDao;
import com.healthtracker.dao.impl.GoalDaoImpl;
import com.healthtracker.model.Goal;
import com.healthtracker.model.User;
import com.healthtracker.service.GoalService;

import java.util.List;

public class GoalServiceImpl implements GoalService {
    private final GoalDao goalDao = new GoalDaoImpl();

    @Override
    public void addGoal(Goal goal) {
        goalDao.save(goal);
    }

    @Override
    public List<Goal> getGoalsByUser(User user) {
        return goalDao.findByUser(user);
    }

    @Override
    public void deleteGoal(Goal goal) {
        goalDao.delete(goal);
    }

    @Override
    public Goal getById(Long id) {
        return goalDao.findById(id);
    }

    @Override
    public void updateGoal(Goal goal) {
        goalDao.update(goal);
    }
}
