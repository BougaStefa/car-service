package com.carservice.controller;

import com.carservice.model.Customer;
import com.carservice.service.CustomerService;
import com.carservice.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for managing the Customer Form view. Handles user interactions, form validation,
 * and communication with the CustomerService.
 */
public class CustomerFormController {
  @FXML private TextField forenameField;
  @FXML private TextField surnameField;
  @FXML private TextField addressField;
  @FXML private TextField postCodeField;
  @FXML private TextField phoneField;

  private CustomerService customerService;
  private Customer customer;
  private boolean isEditMode;
  private Runnable onSaveCallback;

  /** Initializes the controller and sets up the CustomerService instance. */
  public void initialize() {
    customerService = new CustomerService();
  }

  /**
   * Sets the customer to be edited or creates a new customer if null. Populates the form fields
   * with the customer's data if in edit mode.
   *
   * @param customer the customer to edit or null for a new customer
   */
  public void setCustomer(Customer customer) {
    this.customer = customer;
    this.isEditMode = customer != null;

    if (isEditMode) {
      forenameField.setText(customer.getForename());
      surnameField.setText(customer.getSurname());
      addressField.setText(customer.getAddress());
      postCodeField.setText(customer.getPostCode());
      phoneField.setText(customer.getPhoneNo());
    }
  }

  /**
   * Sets a callback to be executed after saving the customer.
   *
   * @param callback the callback to execute
   */
  public void setOnSaveCallback(Runnable callback) {
    this.onSaveCallback = callback;
  }

  /**
   * Handles the save button action. Validates input, saves or updates the customer, and closes the
   * form.
   */
  @FXML
  private void handleSave() {
    if (!validateInput()) {
      return;
    }

    try {
      if (isEditMode) {
        updateCustomer();
      } else {
        createCustomer();
      }

      if (onSaveCallback != null) {
        onSaveCallback.run();
      }

      closeForm();
    } catch (ServiceException e) {
      showError("Error saving customer: " + e.getMessage());
    }
  }

  /**
   * Creates a new customer and saves it using the CustomerService.
   *
   * @throws ServiceException if an error occurs while saving the customer
   */
  private void createCustomer() throws ServiceException {
    Customer newCustomer = new Customer();
    populateCustomerData(newCustomer);
    customerService.save(newCustomer);
  }

  /**
   * Updates the existing customer using the CustomerService.
   *
   * @throws ServiceException if an error occurs while updating the customer
   */
  private void updateCustomer() throws ServiceException {
    populateCustomerData(customer);
    customerService.update(customer);
  }

  /**
   * Populates the customer object with data from the form fields.
   *
   * @param customer the customer object to populate
   */
  private void populateCustomerData(Customer customer) {
    customer.setForename(forenameField.getText().trim());
    customer.setSurname(surnameField.getText().trim());
    customer.setAddress(addressField.getText().trim());
    customer.setPostCode(postCodeField.getText().trim());
    customer.setPhoneNo(phoneField.getText().trim());
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

    if (forenameField.getText().trim().isEmpty()) {
      errors.append("Forename is required\n");
    }
    if (surnameField.getText().trim().isEmpty()) {
      errors.append("Surname is required\n");
    }
    if (addressField.getText().trim().isEmpty()) {
      errors.append("Address is required\n");
    }
    if (!postCodeField.getText().trim().matches("[A-Z0-9]{5,7}")) {
      errors.append("Invalid post code format (5-7 alphanumeric characters)\n");
    }
    if (!phoneField.getText().trim().matches("\\d{10}")) {
      errors.append("Invalid phone number (must be 10 digits)\n");
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
    ((Stage) forenameField.getScene().getWindow()).close();
  }
}
