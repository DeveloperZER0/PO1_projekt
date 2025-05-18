package com.healthtracker.dao;

import com.healthtracker.model.Measurement;
import com.healthtracker.model.User;

import java.util.List;

public interface MeasurementDao extends GenericDao<Measurement>{
    List<Measurement> findByUser(User user);
    /*
    * List<Measurement> findByUserAndType(User user, Class<? extends Measurement> type);
    * List<Measurement> findByUserAndDate(User user, LocalDate date);
    * List<Measurement> findByUserBetweenDates(User user, LocalDate from, LocalDate to);
    */
}
