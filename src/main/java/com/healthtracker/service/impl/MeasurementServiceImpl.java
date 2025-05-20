package com.healthtracker.service.impl;

import com.healthtracker.dao.MeasurementDao;
import com.healthtracker.dao.impl.MeasurementDaoImpl;
import com.healthtracker.model.Measurement;
import com.healthtracker.model.User;
import com.healthtracker.service.MeasurementService;

import java.util.List;

/**
 * Implementacja logiki pomiarów zdrowotnych użytkownika.
 */
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementDao measurementDao = new MeasurementDaoImpl();

    /**
     * Dodaje nowy pomiar do bazy danych.
     */
    @Override
    public void addMeasurement(Measurement measurement) {
        measurementDao.save(measurement);
    }

    /**
     * Zwraca listę wszystkich pomiarów użytkownika.
     */
    @Override
    public List<Measurement> getMeasurementsByUser(User user) {
        return measurementDao.findByUser(user);
    }

    /**
     * Usuwa dany pomiar z bazy.
     */
    @Override
    public void deleteMeasurement(Measurement measurement) {
        measurementDao.delete(measurement);
    }

    /**
     * Zwraca pomiar na podstawie jego ID.
     */
    @Override
    public Measurement getById(Long id) {
        return measurementDao.findById(id);
    }

    @Override
    public void updateMeasurement(Measurement measurement) {
        measurementDao.update(measurement);
    }
}
