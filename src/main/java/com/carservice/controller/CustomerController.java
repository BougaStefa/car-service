package com.carservice.controller;

import com.carservice.model.Customer;
import com.carservice.service.CustomerService;
import com.carservice.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomerController {
  @FXML private TextField searchField;
  @FXML private TableView<Customer> customerTable;
  @FXML private TableColumn<Customer, Long> idColumn;
  @FXML private TableColumn<Customer, String> forenameColumn;
  @FXML private TableColumn<Customer, String> surnameColumn;
  @FXML private TableColumn<Customer, String> addressColumn;
  @FXML private TableColumn<Customer, String> postCodeColumn;
  @FXML private TableColumn<Customer, String> phoneColumn;
  @FXML private TableColumn<Customer, Void> actionsColumn;

  private final CustomerService customerService;

  public CustomerController() {
    customerService = new CustomerService();
  }

  @FXML
  private void initialize() {
    // Initialize columns
    idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    forenameColumn.setCellValueFactory(new PropertyValueFactory<>("forename"));
    surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    postCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postCode"));
    phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));

    setupActionsColumn();
    loadCustomers();

    // Add search listener
    searchField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.trim().isEmpty()) {
                loadCustomers();
              } else {
                searchCustomers(newValue);
              }
            });
  }

  private void setupActionsColumn() {
    actionsColumn.setCellFactory(
        param ->
            new TableCell<>() {
              private final Button editButton = new Button("Edit");
              private final Button deleteButton = new Button("Delete");
              private final HBox container = new HBox(5, editButton, deleteButton);

              {
                editButton.setOnAction(
                    event -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(
                    event -> handleDelete(getTableView().getItems().get(getIndex())));
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
              }
            });
  }

  @FXML
  private void handleSearch() {
    String searchTerm = searchField.getText().trim();
    if (!searchTerm.isEmpty()) {
      searchCustomers(searchTerm);
    }
  }

  private void searchCustomers(String surname) {
    try {
      customerTable.setItems(
          FXCollections.observableArrayList(customerService.findBySurname(surname)));
    } catch (ServiceException e) {
      showError("Search Error", e.getMessage());
    }
  }

  @FXML
  private void handleAddNew() {
    showCustomerDialog(new Customer());
  }

  private void handleEdit(Customer customer) {
    showCustomerDialog(customer);
  }

  private void handleDelete(Customer customer) {
    Alert alert =
        new Alert(
            Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete this customer?",
            ButtonType.YES,
            ButtonType.NO);
    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.YES) {
                try {
                  customerService.delete(customer.getCustomerId());
                  loadCustomers();
                } catch (ServiceException e) {
                  showError("Delete Error", e.getMessage());
                }
              }
            });
  }

  private void showCustomerDialog(Customer customer) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer-dialog.fxml"));
      Scene scene = new Scene(loader.load());

      CustomerDialogController controller = loader.getController();
      controller.setCustomer(customer);

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setTitle(customer.getCustomerId() == null ? "Add Customer" : "Edit Customer");
      stage.setScene(scene);

      stage.showAndWait();
      loadCustomers();
    } catch (Exception e) {
      showError("Dialog Error", "Error showing customer dialog: " + e.getMessage());
    }
  }

  private void loadCustomers() {
    try {
      customerTable.setItems(FXCollections.observableArrayList(customerService.findAll()));
    } catch (ServiceException e) {
      showError("Load Error", e.getMessage());
    }
  }

  private void showError(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
