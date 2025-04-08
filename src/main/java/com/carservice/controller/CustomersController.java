package com.carservice.controller;

import com.carservice.model.Customer;
import com.carservice.service.CustomerService;
import com.carservice.service.JobService;
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

/**
 * Controller class for managing the Customers view. Handles user interactions, data loading, and
 * communication with services.
 */
public class CustomersController {
  private final CustomerService customerService;
  private final JobService jobService;
  private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

  @FXML private TextField searchField;
  @FXML private Button clearFilterButton;
  @FXML private TableView<Customer> customerTable;
  @FXML private TableColumn<Customer, Long> idColumn;
  @FXML private TableColumn<Customer, String> forenameColumn;
  @FXML private TableColumn<Customer, String> surnameColumn;
  @FXML private TableColumn<Customer, String> addressColumn;
  @FXML private TableColumn<Customer, String> postCodeColumn;
  @FXML private TableColumn<Customer, String> phoneColumn;
  @FXML private TableColumn<Customer, Double> avgServiceCostColumn;
  @FXML private TableColumn<Customer, Void> actionsColumn;

  /** Constructor for initializing service dependencies. */
  public CustomersController() {
    this.customerService = new CustomerService();
    this.jobService = new JobService();
  }

  /** Initializes the controller and sets up the UI components. */
  @FXML
  private void initialize() {
    setupTableColumns();
    setupSearchField();
    loadCustomers();
  }

  /** Configures the search field to trigger a search when the Enter key is pressed. */
  private void setupSearchField() {
    searchField.setOnKeyPressed(
        event -> {
          if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            handleSearch();
          }
        });
  }

  /** Configures the table columns with property value factories and custom cell factories. */
  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    forenameColumn.setCellValueFactory(new PropertyValueFactory<>("forename"));
    surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    postCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postCode"));
    phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));

    // Set up average service cost column with a custom value factory
    avgServiceCostColumn.setCellValueFactory(
        cellData -> {
          Customer customer = cellData.getValue();
          try {
            Double avgCost = jobService.getAverageServiceCostByCustomer(customer.getCustomerId());
            return new javafx.beans.property.SimpleDoubleProperty(avgCost != null ? avgCost : 0.0)
                .asObject();
          } catch (ServiceException e) {
            showError("Error calculating average cost: " + e.getMessage());
            return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
          }
        });

    // Format average service cost column to display currency
    avgServiceCostColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  setText(String.format("£%.2f", item));
                }
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

  /** Loads the list of customers and populates the customer table. */
  private void loadCustomers() {
    try {
      List<Customer> customers = customerService.findAll();
      customerList.setAll(customers);
      customerTable.setItems(customerList);
    } catch (ServiceException e) {
      showError("Error loading customers: " + e.getMessage());
    }
  }

  /**
   * Opens the customer form for adding or editing a customer.
   *
   * @param customer the customer to edit, or null for adding a new customer
   */
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

  /** Handles the search action by filtering customers based on the search term. */
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

  /**
   * Handles the action for clearing the search filter. Resets the search field and reloads all
   * customers.
   */
  @FXML
  private void handleClearFilter() {
    searchField.clear();
    loadCustomers();
  }

  /** Handles the action for adding a new customer. Opens the customer form in add mode. */
  @FXML
  public void handleAddCustomer() {
    showCustomerForm(null);
  }

  /**
   * Handles the action for editing a customer. Opens the customer form in edit mode.
   *
   * @param customer the customer to edit
   */
  private void handleEditCustomer(Customer customer) {
    showCustomerForm(customer);
  }

  /**
   * Handles the action for deleting a customer. Prompts the user for confirmation and deletes the
   * customer if confirmed.
   *
   * @param customer the customer to delete
   */
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
