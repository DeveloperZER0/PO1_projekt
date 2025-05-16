package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "weight_measurements")
public class WeightMeasurement extends Measurement {
    // private double weight;
    private double weight;
    // public WeightMeasurement() {}
    public WeightMeasurement() {}
    // get/set dla weight

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // @Override getSummary() zwraca weight + " kg"
    @Override
    public String getSummary() {
        return String.format("%.2f kg", weight);
    }
}
