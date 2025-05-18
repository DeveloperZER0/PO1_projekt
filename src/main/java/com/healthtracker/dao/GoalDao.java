package com.healthtracker.dao;

import com.healthtracker.model.Goal;
import com.healthtracker.model.User;

import java.util.List;

public interface GoalDao extends GenericDao<Goal>{
    List<Goal> findByUser(User user);
    /*
    * List<Goal> findActiveByUser(User user); // tylko cele z datą w przyszłości
    * Goal findLatestGoalByUser(User user);   // najnowszy cel użytkownika
    * */
}
