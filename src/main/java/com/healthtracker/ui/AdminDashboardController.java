package com.healthtracker.ui;

import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import com.healthtracker.service.impl.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Kontroler widoku administratora.
 * Pozwala adminowi przeglądać listę wszystkich użytkowników w systemie.
 */
public class AdminDashboardController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Long> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    private final UserService userService = new UserServiceImpl();

    @FXML
    public void initialize() {
        // Powiązania kolumn z właściwościami obiektu User
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Załaduj dane użytkowników
        userTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
    }
}
