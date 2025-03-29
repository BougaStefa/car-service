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

  public DashboardController() {
    this.customerService = new CustomerService();
    this.carService = new CarService();
    this.jobService = new JobService();
    this.garageService = new GarageService();
    this.activityService = new ActivityService();
  }

  @FXML
  private void initialize() {
    setupRecentActivityTable();
    loadStatistics();
    loadRecentActivity();
  }

  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  private void setupRecentActivityTable() {
    // Setup columns
    timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

    // Format timestamp
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

    // Style based on action type
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

  private void loadRecentActivity() {
    try {
      List<Activity> activities = activityService.getRecentActivity();
      recentActivityTable.setItems(FXCollections.observableArrayList(activities));
    } catch (ServiceException e) {
      showError("Error loading recent activity: " + e.getMessage());
    }
  }

  public void refreshActivity() {
    loadRecentActivity();
  }

  @FXML
  private void navigateToCustomers() {
    mainController.showCustomers();
  }

  @FXML
  private void navigateToCars() {
    mainController.showCars();
  }

  @FXML
  private void navigateToJobs() {
    mainController.showJobs();
  }

  @FXML
  private void navigateToGarages() {
    mainController.showGarages();
  }

  @FXML
  private void addNewCustomer() {
    mainController.showNewCustomerForm();
    // Get the CustomerController instance and call its add method
    // This could be done by exposing a method in MainController to get the current controller
    // or by using an event system
  }

  @FXML
  private void addNewCar() {
    mainController.showNewCarForm();
    // Similar to above, would need access to CarsController to call its add method
  }

  @FXML
  private void addNewJob() {
    mainController.showNewJobForm();
    // Similar to above, would need access to JobsController to call its add method
  }

  @FXML
  private void addNewGarage() {
    mainController.showNewGarageForm();
    // Similar to above, would need access to GaragesController to call its add method
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  // Inner class for recent activity table
  public static class RecentActivity {
    private final LocalDateTime date;
    private final String type;
    private final String description;

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
