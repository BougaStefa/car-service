package com.carservice.controller;

import com.carservice.model.Car;
import com.carservice.model.Customer;
import com.carservice.service.CarService;
import com.carservice.service.CustomerService;
import com.carservice.service.JobService;
import com.carservice.service.ServiceException;
import java.io.IOException;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Controller class for managing the Cars view. Handles user interactions, data loading, and
 * communication with services.
 */
public class CarsController {
  private final CarService carService;
  private final JobService jobService;
  private final CustomerService customerService;
  private final ObservableList<Car> carList = FXCollections.observableArrayList();
  private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

  @FXML private ComboBox<Customer> customerFilter;
  @FXML private Button clearFilterButton;
  @FXML private TableView<Car> carTable;
  @FXML private TableColumn<Car, String> regNoColumn;
  @FXML private TableColumn<Car, String> makeColumn;
  @FXML private TableColumn<Car, String> modelColumn;
  @FXML private TableColumn<Car, Integer> yearColumn;
  @FXML private TableColumn<Car, Customer> customerColumn;
  @FXML private TableColumn<Car, Long> totalServiceDaysColumn;
  @FXML private TableColumn<Car, Void> actionsColumn;

  /** Constructor for initializing service dependencies. */
  public CarsController() {
    this.carService = new CarService();
    this.jobService = new JobService();
    this.customerService = new CustomerService();
  }

  /** Initializes the controller and sets up the UI components. */
  @FXML
  private void initialize() {
    setupCustomerFilter();
    setupTableColumns();
    loadCustomers();
    loadCars();
  }

  /**
   * Configures the customer filter ComboBox with a custom string converter and adds a listener to
   * filter cars by customer.
   */
  private void setupCustomerFilter() {
    customerFilter.setConverter(
        new StringConverter<Customer>() {
          @Override
          public String toString(Customer customer) {
            if (customer == null) return "";
            return customer.getForename() + " " + customer.getSurname();
          }

          @Override
          public Customer fromString(String string) {
            return null; // Not needed for ComboBox
          }
        });

    customerFilter
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal == null) {
                loadCars();
              } else {
                loadCarsByCustomer(newVal);
              }
            });
  }

  /** Configures the table columns with property value factories and custom cell factories. */
  private void setupTableColumns() {
    regNoColumn.setCellValueFactory(new PropertyValueFactory<>("regNo"));
    makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
    modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
    yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
    customerColumn.setCellValueFactory(
        cellData -> {
          Car car = cellData.getValue();
          Long customerId = car.getCustomerId();
          try {
            Customer customer = customerService.findById(customerId);
            return new SimpleObjectProperty<>(customer);
          } catch (ServiceException e) {
            return new SimpleObjectProperty<>(null);
          }
        });

    customerColumn.setCellFactory(
        column ->
            new TableCell<Car, Customer>() {
              @Override
              protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                  setText(null);
                } else {
                  setText(customer.getForename() + " " + customer.getSurname());
                }
              }
            });
    totalServiceDaysColumn.setCellValueFactory(
        cellData -> {
          Car car = cellData.getValue();
          try {
            long days = jobService.calculateTotalServiceDays(car.getRegNo());
            return new SimpleObjectProperty<>(days);
          } catch (ServiceException e) {
            showError("Error calculating service days: " + e.getMessage());
            return new SimpleObjectProperty<>(0L);
          }
        });

    setupActionsColumn();
  }

  /** Configures the actions column with Edit and Delete buttons for each row. */
  private void setupActionsColumn() {
    actionsColumn.setCellFactory(
        param ->
            new TableCell<>() {
              private final Button editButton = new Button("Edit");
              private final Button deleteButton = new Button("Delete");
              private final HBox buttons = new HBox(5, editButton, deleteButton);

              {
                buttons.setAlignment(Pos.CENTER);
                editButton.setOnAction(
                    event -> handleEditCar(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(
                    event -> handleDeleteCar(getTableView().getItems().get(getIndex())));
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
              }
            });
  }

  /** Loads the list of customers and populates the customer filter ComboBox. */
  private void loadCustomers() {
    try {
      List<Customer> customers = customerService.findAll();
      customerList.setAll(customers);
      customerFilter.setItems(customerList);
    } catch (ServiceException e) {
      showError("Error loading customers: " + e.getMessage());
    }
  }

  /** Loads the list of all cars and populates the car table. */
  private void loadCars() {
    try {
      List<Car> cars = carService.findAll();
      carList.setAll(cars);
      carTable.setItems(carList);
    } catch (ServiceException e) {
      showError("Error loading cars: " + e.getMessage());
    }
  }

  /**
   * Loads the list of cars filtered by the selected customer.
   *
   * @param customer the selected customer
   */
  private void loadCarsByCustomer(Customer customer) {
    try {
      List<Car> cars = carService.findByCustomer(customer.getCustomerId());
      carList.setAll(cars);
    } catch (ServiceException e) {
      showError("Error loading cars: " + e.getMessage());
    }
  }

  /** Handles the action for adding a new car. Opens the car form in add mode. */
  @FXML
  public void handleAddCar() {
    showCarForm(null);
  }

  /**
   * Handles the action for clearing the customer filter. Resets the filter and reloads all cars.
   */
  @FXML
  public void handleClearFilter() {
    customerFilter.setValue(null);
    loadCars();
  }

  /**
   * Handles the action for editing a car. Opens the car form in edit mode.
   *
   * @param car the car to edit
   */
  private void handleEditCar(Car car) {
    showCarForm(car);
  }

  /**
   * Handles the action for deleting a car. Prompts the user for confirmation and deletes the car if
   * confirmed.
   *
   * @param car the car to delete
   */
  private void handleDeleteCar(Car car) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Delete");
    alert.setHeaderText("Delete Car");
    alert.setContentText(
        "Are you sure you want to delete car: "
            + car.getMake()
            + " "
            + car.getModel()
            + " ("
            + car.getRegNo()
            + ")?");

    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.OK) {
                try {
                  if (carService.delete(car.getRegNo())) {
                    carList.remove(car);
                    showInfo("Car deleted successfully");
                  }
                } catch (ServiceException e) {
                  showError("Error deleting car: " + e.getMessage());
                }
              }
            });
  }

  /**
   * Opens the car form for adding or editing a car.
   *
   * @param car the car to edit, or null for adding a new car
   */
  private void showCarForm(Car car) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/car-form.fxml"));
      Parent root = loader.load();

      CarFormController controller = loader.getController();
      controller.setCustomers(customerList);
      controller.setCar(car);
      controller.setOnSaveCallback(this::loadCars);

      Stage stage = new Stage();
      stage.setTitle(car == null ? "Add New Car" : "Edit Car");
      stage.setScene(new Scene(root));
      stage.setResizable(false);
      stage.showAndWait();
    } catch (IOException e) {
      showError("Error opening car form: " + e.getMessage());
    }
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

  /**
   * Displays an informational message in an alert dialog.
   *
   * @param message the informational message to display
   */
  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
