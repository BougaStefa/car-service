package com.carservice.controller;

import com.carservice.model.Garage;
import com.carservice.service.GarageService;
import com.carservice.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for managing the Garage Form view. Handles user interactions, form validation,
 * and communication with the GarageService.
 */
public class GarageFormController {
  @FXML private TextField garageNameField;
  @FXML private TextField addressField;
  @FXML private TextField townField;
  @FXML private TextField postCodeField;
  @FXML private TextField phoneField;

  private GarageService garageService;
  private Garage garage;
  private boolean isEditMode;
  private Runnable onSaveCallback;

  /** Initializes the controller and sets up the GarageService instance. */
  @FXML
  public void initialize() {
    garageService = new GarageService();
  }

  /**
   * Sets the garage to be edited or creates a new garage if null. Populates the form fields with
   * the garage's data if in edit mode.
   *
   * @param garage the garage to edit or null for a new garage
   */
  public void setGarage(Garage garage) {
    this.garage = garage;
    this.isEditMode = garage != null;

    if (isEditMode) {
      garageNameField.setText(garage.getGarageName());
      addressField.setText(garage.getAddress());
      townField.setText(garage.getTown());
      postCodeField.setText(garage.getPostCode());
      phoneField.setText(garage.getPhoneNo());
    }
  }

  /**
   * Sets a callback to be executed after saving the garage.
   *
   * @param callback the callback to execute
   */
  public void setOnSaveCallback(Runnable callback) {
    this.onSaveCallback = callback;
  }

  /**
   * Handles the save button action. Validates input, saves or updates the garage, and closes the
   * form.
   */
  @FXML
  private void handleSave() {
    if (!validateInput()) {
      return;
    }

    try {
      if (isEditMode) {
        updateGarage();
      } else {
        createGarage();
      }

      if (onSaveCallback != null) {
        onSaveCallback.run();
      }

      closeForm();
    } catch (ServiceException e) {
      showError("Error saving garage: " + e.getMessage());
    }
  }

  /**
   * Creates a new garage and saves it using the GarageService.
   *
   * @throws ServiceException if an error occurs while saving the garage
   */
  private void createGarage() throws ServiceException {
    Garage newGarage = new Garage();
    populateGarageData(newGarage);
    Long newId = garageService.save(newGarage); // Handles Long return type for ID
    if (newId == null) {
      throw new ServiceException("Failed to save garage");
    }
  }

  /**
   * Updates the existing garage using the GarageService.
   *
   * @throws ServiceException if an error occurs while updating the garage
   */
  private void updateGarage() throws ServiceException {
    populateGarageData(garage);
    if (!garageService.update(garage)) {
      throw new ServiceException("Failed to update garage");
    }
  }

  /**
   * Populates the garage object with data from the form fields.
   *
   * @param garage the garage object to populate
   */
  private void populateGarageData(Garage garage) {
    garage.setGarageName(garageNameField.getText().trim());
    garage.setAddress(addressField.getText().trim());
    garage.setTown(townField.getText().trim());
    garage.setPostCode(postCodeField.getText().trim());
    garage.setPhoneNo(phoneField.getText().trim());
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

    if (garageNameField.getText().trim().isEmpty()) {
      errors.append("Garage name is required\n");
    }
    if (addressField.getText().trim().isEmpty()) {
      errors.append("Address is required\n");
    }
    if (townField.getText().trim().isEmpty()) {
      errors.append("Town is required\n");
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
    ((Stage) garageNameField.getScene().getWindow()).close();
  }
}
