package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "heart_rate_measurements")
public class HeartRateMeasurement extends Measurement {
    // private int bpm;
    // public HeartRateMeasurement() {}
    // get/set dla bpm
    // @Override getSummary() zwraca bpm + " bpm"
}
