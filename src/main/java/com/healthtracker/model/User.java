package com.healthtracker.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
    // pola: id, username, passwordHash, email, role (enum Role)
    // relacje @OneToMany do measurements, activities, meals, goals

    // public User() {}      ← pusty konstruktor
    // public User(String username, String passwordHash, Role role) {}  ← konstruktor wygodny

    // gettery i settery
}
