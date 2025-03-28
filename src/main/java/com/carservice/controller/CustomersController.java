package com.carservice.controller;

import com.carservice.model.Customer;
import com.carservice.service.CustomerService;
import com.carservice.service.ServiceException;
import java.io.IOException;
import java.util.List;
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

public class CustomersController {
  private final CustomerService customerService;
  private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

  @FXML private TextField searchField;
  @FXML private TableView<Customer> customerTable;
  @FXML private TableColumn<Customer, Long> idColumn;
  @FXML private TableColumn<Customer, String> forenameColumn;
  @FXML private TableColumn<Customer, String> surnameColumn;
  @FXML private TableColumn<Customer, String> addressColumn;
  @FXML private TableColumn<Customer, String> postCodeColumn;
  @FXML private TableColumn<Customer, String> phoneColumn;
  @FXML private TableColumn<Customer, Void> actionsColumn;

  public CustomersController() {
    this.customerService = new CustomerService();
  }

  @FXML
  private void initialize() {
    setupTableColumns();
    loadCustomers();
  }

  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    forenameColumn.setCellValueFactory(new PropertyValueFactory<>("forename"));
    surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    postCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postCode"));
    phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));
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
                    event -> handleEditCustomer(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(
                    event -> handleDeleteCustomer(getTableView().getItems().get(getIndex())));
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
      customerTable.setItems(customerList);
    } catch (ServiceException e) {
      showError("Error loading customers: " + e.getMessage());
    }
  }

  private void showCustomerForm(Customer customer) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer-form.fxml"));
      Parent root = loader.load();

      CustomerFormController controller = loader.getController();
      controller.setCustomer(customer);
      controller.setOnSaveCallback(this::loadCustomers);

      Stage stage = new Stage();
      stage.setTitle(customer == null ? "Add New Customer" : "Edit Customer");
      stage.setScene(new Scene(root));
      stage.setResizable(false);
      stage.showAndWait();
    } catch (IOException e) {
      showError("Error opening customer form: " + e.getMessage());
    }
  }

  @FXML
  private void handleSearch() {
    String searchTerm = searchField.getText().trim();
    if (searchTerm.isEmpty()) {
      loadCustomers();
      return;
    }

    try {
      List<Customer> customers = customerService.findBySurname(searchTerm);
      customerList.setAll(customers);
    } catch (ServiceException e) {
      showError("Error searching customers: " + e.getMessage());
    }
  }

  @FXML
  private void handleAddCustomer() {
    showCustomerForm(null);
  }

  private void handleEditCustomer(Customer customer) {
    showCustomerForm(customer);
  }

  private void handleDeleteCustomer(Customer customer) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Delete");
    alert.setHeaderText("Delete Customer");
    alert.setContentText(
        "Are you sure you want to delete customer: "
            + customer.getForename()
            + " "
            + customer.getSurname()
            + "?");

    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.OK) {
                try {
                  if (customerService.delete(customer.getCustomerId())) {
                    customerList.remove(customer);
                    showInfo("Customer deleted successfully");
                  }
                } catch (ServiceException e) {
                  showError("Error deleting customer: " + e.getMessage());
                }
              }
            });
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
