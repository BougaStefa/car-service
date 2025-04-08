package com.carservice.controller;

import com.carservice.model.Car;
import com.carservice.model.Customer;
import com.carservice.service.CarService;
import com.carservice.service.ServiceException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Controller class for managing the Car Form view. Handles user interactions, form validation, and
 * communication with the CarService.
 */
public class CarFormController {
  @FXML private TextField regNoField;
  @FXML private TextField makeField;
  @FXML private TextField modelField;
  @FXML private TextField yearField;
  @FXML private ComboBox<Customer> customerComboBox;

  private CarService carService;
  private Car car;
  private boolean isEditMode;
  private Runnable onSaveCallback;

  /** Initializes the controller and sets up the customer combo box. */
  @FXML
  public void initialize() {
    carService = new CarService();
    setupCustomerComboBox();
  }

  /** Configures the customer combo box with a custom string converter. */
  private void setupCustomerComboBox() {
    customerComboBox.setConverter(
        new StringConverter<Customer>() {
          @Override
          public String toString(Customer customer) {
            if (customer == null) return "";
            return customer.getForename() + " " + customer.getSurname();
          }

          @Override
          public Customer fromString(String string) {
            return null;
          }
        });
  }

  /**
   * Sets the list of customers to be displayed in the combo box.
   *
   * @param customers the list of customers
   */
  public void setCustomers(ObservableList<Customer> customers) {
    customerComboBox.setItems(customers);
  }

  /**
   * Sets the car to be edited or creates a new car if null. Populates the form fields with the
   * car's data if in edit mode.
   *
   * @param car the car to edit or null for a new car
   */
  public void setCar(Car car) {
    this.car = car;
    this.isEditMode = car != null;

    if (isEditMode) {
      regNoField.setText(car.getRegNo());
      regNoField.setDisable(true); // Registration number shouldn't be editable
      makeField.setText(car.getMake());
      modelField.setText(car.getModel());
      yearField.setText(String.valueOf(car.getYear()));

      // Find and set the customer in the combo box based on customerId
      for (Customer customer : customerComboBox.getItems()) {
        if (customer.getCustomerId().equals(car.getCustomerId())) {
          customerComboBox.setValue(customer);
          break;
        }
      }
    }
  }

  /**
   * Sets a callback to be executed after saving the car.
   *
   * @param callback the callback to execute
   */
  public void setOnSaveCallback(Runnable callback) {
    this.onSaveCallback = callback;
  }

  /**
   * Handles the save button action. Validates input, saves or updates the car, and closes the form.
   */
  @FXML
  private void handleSave() {
    if (!validateInput()) {
      return;
    }

    try {
      if (isEditMode) {
        updateCar();
      } else {
        createCar();
      }

      if (onSaveCallback != null) {
        onSaveCallback.run();
      }

      closeForm();
    } catch (ServiceException e) {
      showError("Error saving car: " + e.getMessage());
    }
  }

  /**
   * Creates a new car and saves it using the CarService.
   *
   * @throws ServiceException if an error occurs while saving the car
   */
  private void createCar() throws ServiceException {
    Car newCar = new Car();
    populateCarData(newCar);
    carService.save(newCar);
  }

  /**
   * Updates the existing car using the CarService.
   *
   * @throws ServiceException if an error occurs while updating the car
   */
  private void updateCar() throws ServiceException {
    populateCarData(car);
    carService.update(car);
  }

  /**
   * Populates the car object with data from the form fields.
   *
   * @param car the car object to populate
   */
  private void populateCarData(Car car) {
    car.setRegNo(regNoField.getText().trim().toUpperCase());
    car.setMake(makeField.getText().trim());
    car.setModel(modelField.getText().trim());
    car.setYear(Integer.parseInt(yearField.getText().trim()));
    car.setCustomerId(customerComboBox.getValue().getCustomerId());
  }

  /** Handles the cancel button action and closes the form. */
  @FXML
  private void handleCancel() {
    closeForm();
  }

  /**
   * Validates the input fields and displays error messages if invalid.
   *
   * @return true if the input is valid, false otherwise
   */
  private boolean validateInput() {
    StringBuilder errors = new StringBuilder();

    if (regNoField.getText().trim().isEmpty()) {
      errors.append("Registration number is required\n");
    } else if (!regNoField.getText().trim().matches("[A-Z0-9]{1,10}")) {
      errors.append("Invalid registration number format\n");
    }

    if (makeField.getText().trim().isEmpty()) {
      errors.append("Make is required\n");
    }

    if (modelField.getText().trim().isEmpty()) {
      errors.append("Model is required\n");
    }

    try {
      int year = Integer.parseInt(yearField.getText().trim());
      if (year < 1900 || year > 2100) {
        errors.append("Year must be between 1900 and 2100\n");
      }
    } catch (NumberFormatException e) {
      errors.append("Invalid year format\n");
    }

    if (customerComboBox.getValue() == null) {
      errors.append("Customer must be selected\n");
    }

    if (errors.length() > 0) {
      showError("Please correct the following errors:\n" + errors.toString());
      return false;
    }

    return true;
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

  /** Closes the form by closing the current stage. */
  private void closeForm() {
    ((Stage) regNoField.getScene().getWindow()).close();
  }
}
