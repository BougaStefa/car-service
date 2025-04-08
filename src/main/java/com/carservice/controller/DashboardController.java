package com.carservice.controller;

import com.carservice.model.Activity;
import com.carservice.service.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller class for managing the Dashboard view. Handles the display of statistics, recent
 * activities, and navigation to other views.
 */
public class DashboardController {
  private final CustomerService customerService;
  private final CarService carService;
  private final JobService jobService;
  private final GarageService garageService;
  private final ActivityService activityService;

  @FXML private Label customerCount;
  @FXML private Label carCount;
  @FXML private Label activeJobCount;
  @FXML private Label garageCount;
  @FXML private TableView<Activity> recentActivityTable;
  @FXML private TableColumn<Activity, LocalDateTime> timestampColumn;
  @FXML private TableColumn<Activity, String> typeColumn;
  @FXML private TableColumn<Activity, String> actionColumn;
  @FXML private TableColumn<Activity, String> descriptionColumn;

  private MainController mainController;

  /** Constructor for initializing service dependencies. */
  public DashboardController() {
    this.customerService = new CustomerService();
    this.carService = new CarService();
    this.jobService = new JobService();
    this.garageService = new GarageService();
    this.activityService = new ActivityService();
  }

  /** Initializes the controller and sets up the dashboard components. */
  @FXML
  private void initialize() {
    setupRecentActivityTable();
    loadStatistics();
    loadRecentActivity();
  }

  /**
   * Sets the main controller for navigation purposes.
   *
   * @param mainController the main controller instance
   */
  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  /** Configures the recent activity table with column mappings and custom cell factories. */
  private void setupRecentActivityTable() {
    // Map columns to Activity properties
    timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

    // Format the timestamp column to display a readable date-time format
    timestampColumn.setCellFactory(
        column ->
            new TableCell<>() {
              private final DateTimeFormatter formatter =
                  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

              @Override
              protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  setText(formatter.format(item));
                }
              }
            });

    // Style the action column based on the action type
    actionColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                  setStyle("");
                } else {
                  setText(item);
                  switch (item) {
                    case "CREATE":
                      setStyle("-fx-text-fill: green;");
                      break;
                    case "UPDATE":
                      setStyle("-fx-text-fill: blue;");
                      break;
                    case "DELETE":
                      setStyle("-fx-text-fill: red;");
                      break;
                    default:
                      setStyle("");
                  }
                }
              }
            });
  }

  /**
   * Loads and displays statistics such as customer count, car count, active jobs, and garage count.
   */
  private void loadStatistics() {
    try {
      customerCount.setText(String.valueOf(customerService.findAll().size()));
      carCount.setText(String.valueOf(carService.findAll().size()));
      garageCount.setText(String.valueOf(garageService.findAll().size()));

      // Count only active jobs (where dateOut is null)
      long activeJobs =
          jobService.findAll().stream().filter(job -> job.getDateOut() == null).count();
      activeJobCount.setText(String.valueOf(activeJobs));
    } catch (ServiceException e) {
      showError("Error loading statistics: " + e.getMessage());
    }
  }

  /** Loads and displays the recent activity in the activity table. */
  private void loadRecentActivity() {
    try {
      List<Activity> activities = activityService.getRecentActivity();
      recentActivityTable.setItems(FXCollections.observableArrayList(activities));
    } catch (ServiceException e) {
      showError("Error loading recent activity: " + e.getMessage());
    }
  }

  /** Refreshes the recent activity table. */
  public void refreshActivity() {
    loadRecentActivity();
  }

  /** Navigates to the Customers view. */
  @FXML
  private void navigateToCustomers() {
    mainController.showCustomers();
  }

  /** Navigates to the Cars view. */
  @FXML
  private void navigateToCars() {
    mainController.showCars();
  }

  /** Navigates to the Jobs view. */
  @FXML
  private void navigateToJobs() {
    mainController.showJobs();
  }

  /** Navigates to the Garages view. */
  @FXML
  private void navigateToGarages() {
    mainController.showGarages();
  }

  /** Opens the form to add a new customer. */
  @FXML
  private void addNewCustomer() {
    mainController.showNewCustomerForm();
  }

  /** Opens the form to add a new car. */
  @FXML
  private void addNewCar() {
    mainController.showNewCarForm();
  }

  /** Opens the form to add a new job. */
  @FXML
  private void addNewJob() {
    mainController.showNewJobForm();
  }

  /** Opens the form to add a new garage. */
  @FXML
  private void addNewGarage() {
    mainController.showNewGarageForm();
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

  /** Inner class representing a recent activity entry. */
  public static class RecentActivity {
    private final LocalDateTime date;
    private final String type;
    private final String description;

    /**
     * Constructs a RecentActivity instance.
     *
     * @param date the date of the activity
     * @param type the type of the activity
     * @param description the description of the activity
     */
    public RecentActivity(LocalDateTime date, String type, String description) {
      this.date = date;
      this.type = type;
      this.description = description;
    }

    public LocalDateTime getDate() {
      return date;
    }

    public String getType() {
      return type;
    }

    public String getDescription() {
      return description;
    }
  }
}
