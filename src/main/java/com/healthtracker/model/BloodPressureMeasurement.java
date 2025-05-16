package com.healthtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bp_measurements")
public class BloodPressureMeasurement extends Measurement {
    // private int systolic, diastolic;
    private int systolic;
    private int diastolic;
    // public BloodPressureMeasurement() {}
    public BloodPressureMeasurement() {}
    // get/set dla obu

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    // @Override getSummary() zwraca "systolic/diastolic mmHg"
    @Override
    public String getSummary() {
        return systolic +" / "+ diastolic + " mmHg";
    }
}
