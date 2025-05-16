package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bp_measurements")
public class BloodPressureMeasurement extends Measurement {
    // private int systolic, diastolic;
    // public BloodPressureMeasurement() {}
    // get/set dla obu
    // @Override getSummary() zwraca "systolic/diastolic mmHg"
}
