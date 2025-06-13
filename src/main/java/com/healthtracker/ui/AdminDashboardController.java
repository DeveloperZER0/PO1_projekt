package com.healthtracker.ui;

import com.healthtracker.model.Role;
import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import com.healthtracker.service.impl.UserServiceImpl;
import com.healthtracker.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Kontroler widoku administratora.
 * Pozwala adminowi przeglądać listę wszystkich użytkowników w systemie
 * i zarządzać ich danymi.
 */
public class AdminDashboardController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private Label totalUsersLabel;
    @FXML private Label regularUsersLabel;
    @FXML private Label adminUsersLabel;

    private final UserService userService = new UserServiceImpl();

    @FXML
    public void initialize() {
        setupUserTable();
        setupDoubleClickHandler();
        setupContextMenu();
        loadUserData();
        updateStatistics();
    }

    private void setupUserTable() {
        // Powiązania kolumn z właściwościami obiektu User
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Dodaj klasę CSS dla spójnego stylu
        userTable.getStyleClass().add("data-table");

        // Ustaw proporcjonalne szerokości kolumn (bez daty utworzenia)
        idColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.1));
        usernameColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.3));
        emailColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.45));
        roleColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.15));
    }

    private void setupDoubleClickHandler() {
        userTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    openUserDetails(selectedUser);
                }
            }
        });
    }

    private void setupContextMenu() {
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem detailsItem = new MenuItem("Szczegóły użytkownika");
            detailsItem.setOnAction(event -> {
                User selectedUser = row.getItem();
                if (selectedUser != null) {
                    openUserDetails(selectedUser);
                }
            });
            
            MenuItem deleteItem = new MenuItem("Usuń użytkownika");
            deleteItem.setOnAction(event -> {
                User selectedUser = row.getItem();
                if (selectedUser != null && canDeleteUser(selectedUser)) {
                    deleteUser(selectedUser);
                }
            });
            
            // Zabezpieczenie - aktualizuj stan przycisku przy każdej zmianie wiersza
            row.itemProperty().addListener((obs, oldUser, newUser) -> {
                updateDeleteItemState(deleteItem, newUser);
            });
            
            // Dodatkowo aktualizuj przy pierwszym utworzeniu
            if (row.getItem() != null) {
                updateDeleteItemState(deleteItem, row.getItem());
            }
            
            contextMenu.getItems().addAll(detailsItem, new SeparatorMenuItem(), deleteItem);
            
            row.contextMenuProperty().bind(
                javafx.beans.binding.Bindings
                    .when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(contextMenu)
            );
            
            // Podświetl własne konto
            row.itemProperty().addListener((obs, oldUser, newUser) -> {
                if (newUser != null && newUser.equals(SessionManager.getCurrentUser())) {
                    row.setStyle("-fx-background-color: #e8f5e8;");
                } else {
                    row.setStyle("");
                }
            });
            
            return row;
        });
    }

    private void updateDeleteItemState(MenuItem deleteItem, User user) {
        if (user != null && !canDeleteUser(user)) {
            deleteItem.setDisable(true);
            if (user.equals(SessionManager.getCurrentUser())) {
                deleteItem.setText("🚫 Nie można usunąć własnego konta");
            } else {
                deleteItem.setText("🚫 Nie można usunąć tego użytkownika");
            }
        } else {
            deleteItem.setDisable(false);
            deleteItem.setText("🗑️ Usuń użytkownika");
        }
    }

    private boolean canDeleteUser(User user) {
        User currentUser = SessionManager.getCurrentUser();
        
        // Nie można usunąć siebie
        if (user.equals(currentUser)) {
            return false;
        }
        
        // Administrator nie może usunąć innego administratora (opcjonalne - usuń tę linię jeśli chcesz pozwolić)
        if (currentUser.getRole() == Role.ADMIN && user.getRole() == Role.ADMIN) {
            return false;
        }
        
        return true;
    }

    private void openUserDetails(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/healthtracker/views/admin_user_details.fxml"));
            Parent root = loader.load();
            
            AdminUserDetailsController controller = loader.getController();
            controller.setUser(user);
            
            Stage stage = new Stage();
            stage.setTitle("Szczegóły użytkownika: " + user.getUsername());
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Błąd podczas otwierania szczegółów użytkownika: " + e.getMessage());
        }
    }

    private void deleteUser(User user) {
        // Dodatkowe zabezpieczenie na poziomie metody
        if (!canDeleteUser(user)) {
            showError("Nie można usunąć tego użytkownika!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
            "Czy na pewno chcesz usunąć użytkownika?\n\n" +
            "Login: " + user.getUsername() + "\n" +
            "Email: " + user.getEmail() + "\n\n" +
            "UWAGA: Zostaną usunięte wszystkie dane użytkownika (pomiary, aktywności, posiłki, cele)!",
            ButtonType.YES, ButtonType.NO);
        
        confirmAlert.setTitle("Potwierdzenie usunięcia użytkownika");
        confirmAlert.setHeaderText("Usuwanie użytkownika");
        
        confirmAlert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                try {
                    userService.deleteUser(user);
                    loadUserData();
                    updateStatistics();
                    showSuccess("Użytkownik został usunięty pomyślnie.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Błąd podczas usuwania użytkownika: " + e.getMessage());
                }
            }
        });
    }

    private void loadUserData() {
        try {
            userTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Błąd podczas ładowania danych użytkowników: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            var users = userService.getAllUsers();
            int totalUsers = users.size();
            long regularUsers = users.stream().filter(u -> u.getRole().name().equals("USER")).count();
            long adminUsers = users.stream().filter(u -> u.getRole().name().equals("ADMIN")).count();
            
            // Aktualizacja - tylko liczby
            totalUsersLabel.setText(String.valueOf(totalUsers));
            regularUsersLabel.setText(String.valueOf(regularUsers));
            adminUsersLabel.setText(String.valueOf(adminUsers));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRefreshClicked() {
        loadUserData();
        updateStatistics();
    }

    @FXML
    private void onLogoutClicked() {
        SessionManager.logout();
        SceneManager.switchScene("/com/healthtracker/views/login.fxml", "Logowanie");
    }

    @FXML
    private void onBackToDashboardClicked() {
        SceneManager.switchScene("/com/healthtracker/views/user_dashboard.fxml", "Panel użytkownika");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Błąd");
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Sukces");
        alert.showAndWait();
    }
}
