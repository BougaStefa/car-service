package com.carservice.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MainController {
  @FXML private StackPane contentArea;
  @FXML private Label statusLabel;
  @FXML private Label timeLabel;
  @FXML private Label userLabel;
  @FXML private HBox statusBar;

  private CustomersController customersController;
  private CarsController carsController;
  private JobsController jobsController;
  private GaragesController garagesController;
  private final String currentUser;
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public MainController() {
    this.currentUser = "BougaStefa"; // In a real app, this would come from authentication
  }

  @FXML
  private void initialize() {
    setupStatusBar();
    showDashboard();
  }

  private void setupStatusBar() {
    // Setup clock update
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateClock()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    // Set user label
    userLabel.setText("User: " + currentUser);

    // Initial clock update
    updateClock();
  }

  private void updateClock() {
    timeLabel.setText("UTC: " + LocalDateTime.now().format(DATE_FORMATTER));
  }

  @FXML
  public void showCustomers() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers-view.fxml"));
      Parent view = loader.load();
      customersController = loader.getController();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(view);
      updateStatus("Viewing Customers");
    } catch (IOException e) {
      showError("Error loading customers view: " + e.getMessage());
    }
  }

  @FXML
  public void showCars() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cars-view.fxml"));
      Parent view = loader.load();
      carsController = loader.getController();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(view);
      updateStatus("Viewing Cars");
    } catch (IOException e) {
      showError("Error loading cars view: " + e.getMessage());
    }
  }

  @FXML
  public void showGarages() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/garages-view.fxml"));
      Parent view = loader.load();
      garagesController = loader.getController();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(view);
      updateStatus("Viewing Garages");
    } catch (IOException e) {
      showError("Error loading garages view: " + e.getMessage());
    }
  }

  @FXML
  public void showJobs() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/jobs-view.fxml"));
      Parent view = loader.load();
      jobsController = loader.getController();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(view);
      updateStatus("Viewing Jobs");
    } catch (IOException e) {
      showError("Error loading jobs view: " + e.getMessage());
    }
  }

  public void showNewCustomerForm() {
    showCustomers();
    if (customersController != null) {
      customersController.handleAddCustomer();
    }
  }

  public void showNewCarForm() {
    showCars();
    if (carsController != null) {
      carsController.handleAddCar();
    }
  }

  public void showNewJobForm() {
    showJobs();
    if (jobsController != null) {
      jobsController.handleAddJob();
    }
  }

  public void showNewGarageForm() {
    showGarages();
    if (garagesController != null) {
      garagesController.handleAddGarage();
    }
  }

  @FXML
  private void handleExit() {
    System.exit(0);
  }

  @FXML
  private void showAbout() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("About Car Service Management");
    alert.setHeaderText(null);
    alert.setContentText("Car Service Management System\nVersion 1.0\n© 2025");
    alert.showAndWait();
  }

  private void showDashboard() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard-view.fxml"));
      Parent view = loader.load();
      DashboardController dashboardController = loader.getController();
      dashboardController.setMainController(this);
      contentArea.getChildren().clear();
      contentArea.getChildren().add(view);
      updateStatus("Welcome to Car Service Management System");
    } catch (IOException e) {
      showError("Error loading dashboard: " + e.getMessage());
    }
  }

  public void updateStatus(String message) {
    statusLabel.setText(message);
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
