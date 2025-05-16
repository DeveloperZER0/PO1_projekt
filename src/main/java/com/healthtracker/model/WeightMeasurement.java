package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "weight_measurements")
public class WeightMeasurement extends Measurement {
    // private double weight;
    // public WeightMeasurement() {}
    // get/set dla weight
    // @Override getSummary() zwraca weight + " kg"
}
