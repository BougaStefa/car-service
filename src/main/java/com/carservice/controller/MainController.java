package com.carservice.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class MainController {
  @FXML private Label statusLabel;

  @FXML
  private void initialize() {
    // Initialization code if needed
    updateStatusLabel("Ready");
  }

  @FXML
  private void handleExit() {
    Platform.exit();
  }

  @FXML
  private void handleAbout() {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("About Car Service Management");
    alert.setHeaderText(null);
    alert.setContentText("Car Service Management System\nVersion 1.0\n\nDeveloped by BougaStefa");
    alert.showAndWait();
  }

  private void updateStatusLabel(String status) {
    statusLabel.setText(status);
  }
}
