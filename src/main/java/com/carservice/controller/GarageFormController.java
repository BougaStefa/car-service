package com.carservice.controller;

import com.carservice.model.Garage;
import com.carservice.service.GarageService;
import com.carservice.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

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

    @FXML
    public void initialize() {
        garageService = new GarageService();
    }

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

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

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

    private void createGarage() throws ServiceException {
        Garage newGarage = new Garage();
        populateGarageData(newGarage);
        Long newId = garageService.save(newGarage); // Changed to handle Long return type
        if (newId == null) {
            throw new ServiceException("Failed to save garage");
        }
    }

    private void updateGarage() throws ServiceException {
        populateGarageData(garage);
        if (!garageService.update(garage)) {
            throw new ServiceException("Failed to update garage");
        }
    }

    private void populateGarageData(Garage garage) {
        garage.setGarageName(garageNameField.getText().trim());
        garage.setAddress(addressField.getText().trim());
        garage.setTown(townField.getText().trim());
        garage.setPostCode(postCodeField.getText().trim());
        garage.setPhoneNo(phoneField.getText().trim());
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeForm() {
        ((Stage) garageNameField.getScene().getWindow()).close();
    }
}
