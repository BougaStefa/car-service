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

/**
 * Main controller class for the Car Service Management application. Manages navigation between
 * views, status updates, and user interface elements.
 */
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

  /**
   * Constructor for initializing the current user. In a real application, this would be dynamically
   * set based on authentication.
   */
  public MainController() {
    this.currentUser = "BougaStefa"; // Placeholder for authenticated user
  }

  /** Initializes the controller and sets up the status bar and dashboard view. */
  @FXML
  private void initialize() {
    setupStatusBar();
    showDashboard();
  }

  /** Sets up the status bar with a clock and user information. */
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

  /** Updates the clock label with the current UTC time. */
  private void updateClock() {
    timeLabel.setText("UTC: " + LocalDateTime.now().format(DATE_FORMATTER));
  }

  /**
   * Displays the Customers view. Loads the FXML file and sets the content area to the Customers
   * view.
   */
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

  /** Displays the Cars view. Loads the FXML file and sets the content area to the Cars view. */
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

  /**
   * Displays the Garages view. Loads the FXML file and sets the content area to the Garages view.
   */
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

  /** Displays the Jobs view. Loads the FXML file and sets the content area to the Jobs view. */
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

  /**
   * Opens the form to add a new customer. Navigates to the Customers view and triggers the add
   * customer action.
   */
  public void showNewCustomerForm() {
    showCustomers();
    if (customersController != null) {
      customersController.handleAddCustomer();
    }
  }

  /**
   * Opens the form to add a new car. Navigates to the Cars view and triggers the add car action.
   */
  public void showNewCarForm() {
    showCars();
    if (carsController != null) {
      carsController.handleAddCar();
    }
  }

  /**
   * Opens the form to add a new job. Navigates to the Jobs view and triggers the add job action.
   */
  public void showNewJobForm() {
    showJobs();
    if (jobsController != null) {
      jobsController.handleAddJob();
    }
  }

  /**
   * Opens the form to add a new garage. Navigates to the Garages view and triggers the add garage
   * action.
   */
  public void showNewGarageForm() {
    showGarages();
    if (garagesController != null) {
      garagesController.handleAddGarage();
    }
  }

  /** Exits the application. */
  @FXML
  private void handleExit() {
    System.exit(0);
  }

  /** Displays the About dialog with application information. */
  @FXML
  private void showAbout() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("About Car Service Management");
    alert.setHeaderText(null);
    alert.setContentText("Car Service Management System\nVersion 1.0\n© 2025");
    alert.showAndWait();
  }

  /**
   * Displays the Dashboard view. Loads the FXML file and sets the content area to the Dashboard
   * view.
   */
  @FXML
  public void showDashboard() {
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

  /**
   * Updates the status label with the provided message.
   *
   * @param message the status message to display
   */
  public void updateStatus(String message) {
    statusLabel.setText(message);
  }

  /**
   * Displays an error message in an alert dialog.
   *
   * @param message the error message to display
   */
  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
