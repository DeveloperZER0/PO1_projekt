package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "heart_rate_measurements")
public class HeartRateMeasurement extends Measurement {
    // private int bpm;
    private int bpm;
    // public HeartRateMeasurement() {}
    public HeartRateMeasurement() {}
    // get/set dla bpm

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    // @Override getSummary() zwraca bpm + " bpm"
    @Override
    public String getSummary() {
        return bpm + " bpm";
    }
}
