package com.healthtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "measurements")
public abstract class Measurement {
    // protected Long id; @Id @GeneratedValue
    // protected LocalDateTime timestamp;
    // @ManyToOne protected User user;

    // public Measurement() {}     ← pusty
    // public abstract String getSummary();  ← do wyświetlania w tabeli

    // gettery / settery
}
