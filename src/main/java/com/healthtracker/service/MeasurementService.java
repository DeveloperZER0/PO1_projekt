package com.healthtracker.service;

import com.healthtracker.model.Measurement;
import com.healthtracker.model.User;

import java.util.List;

/**
 * Interfejs ogólny dla logiki obsługującej pomiary zdrowotne.
 */
public interface MeasurementService {

    /**
     * Dodaje nowy pomiar.
     */
    void addMeasurement(Measurement measurement);

    /**
     * Zwraca wszystkie pomiary zalogowanego użytkownika.
     */
    List<Measurement> getMeasurementsByUser(User user);

    /**
     * Usuwa pomiar.
     */
    void deleteMeasurement(Measurement measurement);

    /**
     * Pobiera pomiar po ID.
     */
    Measurement getById(Long id);

    // opcjonalnie:
    // List<Measurement> getMeasurementsByType(User user, Class<? extends Measurement> type);
    void updateMeasurement(Measurement measurement);
}
