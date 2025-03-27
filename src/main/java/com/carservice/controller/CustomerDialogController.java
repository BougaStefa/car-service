package com.carservice.controller;

import com.carservice.model.Customer;
import com.carservice.service.CustomerService;
import com.carservice.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CustomerDialogController {
  @FXML private TextField forenameField;
  @FXML private TextField surnameField;
  @FXML private TextField addressField;
  @FXML private TextField postCodeField;
  @FXML private TextField phoneField;

  private Customer customer;
  private final CustomerService customerService;

  public CustomerDialogController() {
    customerService = new CustomerService();
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
    populateFields();
  }

  private void populateFields() {
    if (customer != null) {
      forenameField.setText(customer.getForename());
      surnameField.setText(customer.getSurname());
      addressField.setText(customer.getAddress());
      postCodeField.setText(customer.getPostCode());
      phoneField.setText(customer.getPhoneNo());
    }
  }

  @FXML
  private void handleSave() {
    try {
      updateCustomerFromFields();
      if (customer.getCustomerId() == null) {
        customerService.save(customer);
      } else {
        customerService.update(customer);
      }
      closeDialog();
    } catch (ServiceException e) {
      showError("Save Error", e.getMessage());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
  }

  private void updateCustomerFromFields() {
    customer.setForename(forenameField.getText());
    customer.setSurname(surnameField.getText());
    customer.setAddress(addressField.getText());
    customer.setPostCode(postCodeField.getText());
    customer.setPhoneNo(phoneField.getText());
  }

  private void showError(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.showAndWait();
  }

  private void closeDialog() {
    ((Stage) forenameField.getScene().getWindow()).close();
  }
}
