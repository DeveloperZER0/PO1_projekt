package com.healthtracker;

import com.healthtracker.util.HibernateUtil;
import com.healthtracker.ui.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Health Tracker"); // Zmień na sensowny tytuł
        stage.setScene(scene);
        stage.show();
        SceneManager.setStage(stage);
    }

    public static void main(String[] args) {
        // Przenieś test połączenia do osobnej metody lub usuń
        testDatabaseConnection();
        launch();
    }
    
    private static void testDatabaseConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("✔ Połączenie działa!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}