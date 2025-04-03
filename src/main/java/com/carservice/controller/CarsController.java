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

public class CarsController {
  private final CarService carService;
  private final JobService jobService;
  private final CustomerService customerService;
  private final ObservableList<Car> carList = FXCollections.observableArrayList();
  private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

  @FXML private ComboBox<Customer> customerFilter;
  @FXML private TableView<Car> carTable;
  @FXML private TableColumn<Car, String> regNoColumn;
  @FXML private TableColumn<Car, String> makeColumn;
  @FXML private TableColumn<Car, String> modelColumn;
  @FXML private TableColumn<Car, Integer> yearColumn;
  @FXML private TableColumn<Car, Customer> customerColumn;
  @FXML private TableColumn<Car, Long> totalServiceDaysColumn;
  @FXML private TableColumn<Car, Void> actionsColumn;

  public CarsController() {
    this.carService = new CarService();
    this.jobService = new JobService();
    this.customerService = new CustomerService();
  }

  @FXML
  private void initialize() {
    setupCustomerFilter();
    setupTableColumns();
    loadCustomers();
    loadCars();
  }

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

  private void loadCustomers() {
    try {
      List<Customer> customers = customerService.findAll();
      customerList.setAll(customers);
      customerFilter.setItems(customerList);
    } catch (ServiceException e) {
      showError("Error loading customers: " + e.getMessage());
    }
  }

  private void loadCars() {
    try {
      List<Car> cars = carService.findAll();
      carList.setAll(cars);
      carTable.setItems(carList);
    } catch (ServiceException e) {
      showError("Error loading cars: " + e.getMessage());
    }
  }

  private void loadCarsByCustomer(Customer customer) {
    try {
      List<Car> cars = carService.findByCustomer(customer.getCustomerId());
      carList.setAll(cars);
    } catch (ServiceException e) {
      showError("Error loading cars: " + e.getMessage());
    }
  }

  @FXML
  public void handleAddCar() {
    showCarForm(null);
  }

  private void handleEditCar(Car car) {
    showCarForm(car);
  }

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

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
