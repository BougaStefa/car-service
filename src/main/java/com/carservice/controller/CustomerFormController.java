package com.carservice.controller;

import com.carservice.model.Customer;
import com.carservice.service.CustomerService;
import com.carservice.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

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

    public void initialize() {
        customerService = new CustomerService();
    }

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

    private void createCustomer() throws ServiceException {
        Customer newCustomer = new Customer();
        populateCustomerData(newCustomer);
        customerService.save(newCustomer);
    }

    private void updateCustomer() throws ServiceException {
        populateCustomerData(customer);
        customerService.update(customer);
    }

    private void populateCustomerData(Customer customer) {
        customer.setForename(forenameField.getText().trim());
        customer.setSurname(surnameField.getText().trim());
        customer.setAddress(addressField.getText().trim());
        customer.setPostCode(postCodeField.getText().trim());
        customer.setPhoneNo(phoneField.getText().trim());
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeForm() {
        ((Stage) forenameField.getScene().getWindow()).close();
    }
}
