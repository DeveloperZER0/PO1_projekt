package com.healthtracker.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Klasa do przełączania widoków w aplikacji JavaFX.
 */
public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root, 1200, 800));
            primaryStage.show();
        } catch (IOException e) {
            // Dodaj logger zamiast printStackTrace
            System.err.println("Błąd podczas ładowania sceny: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
