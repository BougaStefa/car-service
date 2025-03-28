package com.carservice.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

public class MainController {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Label statusLabel;

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void showCustomers() {
        loadView("/fxml/customers-view.fxml", "Customers");
    }

    @FXML
    private void showCars() {
        loadView("/fxml/cars-view.fxml", "Cars");
    }

    @FXML
    private void showGarages() {
        loadView("/fxml/garages-view.fxml", "Garages");
    }

    @FXML
    private void showJobs() {
        loadView("/fxml/jobs-view.fxml", "Jobs");
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Car Service Management");
        alert.setHeaderText(null);
        alert.setContentText("Car Service Management System\nVersion 1.0\nCreated by: " + System.getProperty("user.name"));
        alert.showAndWait();
    }

    private void loadView(String fxmlPath, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            updateStatus("Viewing " + status);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading view: " + e.getMessage());
        }
    }

    private void updateStatus(String status) {
        statusLabel.setText(status);
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
