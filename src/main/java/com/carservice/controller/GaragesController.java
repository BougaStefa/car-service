package com.carservice.controller;

import com.carservice.model.Garage;
import com.carservice.service.GarageService;
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
 * Controller class for managing the Garages view. Handles user interactions, data loading, and
 * communication with the GarageService.
 */
public class GaragesController {
  private final GarageService garageService;
  private final ObservableList<Garage> garageList = FXCollections.observableArrayList();

  @FXML private TextField searchField;
  @FXML private Button clearFilterButton;
  @FXML private TableView<Garage> garageTable;
  @FXML private TableColumn<Garage, Long> idColumn;
  @FXML private TableColumn<Garage, String> nameColumn;
  @FXML private TableColumn<Garage, String> addressColumn;
  @FXML private TableColumn<Garage, String> townColumn;
  @FXML private TableColumn<Garage, String> postCodeColumn;
  @FXML private TableColumn<Garage, String> phoneColumn;
  @FXML private TableColumn<Garage, Void> actionsColumn;

  /** Constructor for initializing the GarageService dependency. */
  public GaragesController() {
    this.garageService = new GarageService();
  }

  /** Initializes the controller and sets up the UI components. */
  @FXML
  private void initialize() {
    setupTableColumns();
    setupSearchField();
    loadGarages();
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

  /** Configures the table columns with property value factories and sets up the actions column. */
  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("garageId"));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("garageName"));
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    townColumn.setCellValueFactory(new PropertyValueFactory<>("town"));
    postCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postCode"));
    phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));

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
                    event -> handleEditGarage(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(
                    event -> handleDeleteGarage(getTableView().getItems().get(getIndex())));
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
              }
            });
  }

  /** Loads the list of garages and populates the garage table. */
  private void loadGarages() {
    try {
      List<Garage> garages = garageService.findAll();
      garageList.setAll(garages);
      garageTable.setItems(garageList);
    } catch (ServiceException e) {
      showError("Error loading garages: " + e.getMessage());
    }
  }

  /** Handles the search action by filtering garages based on the search term. */
  @FXML
  private void handleSearch() {
    String searchTerm = searchField.getText().trim();
    if (searchTerm.isEmpty()) {
      loadGarages();
      return;
    }

    try {
      List<Garage> garages = garageService.findByName(searchTerm);
      garageList.setAll(garages);
    } catch (ServiceException e) {
      showError("Error searching garages: " + e.getMessage());
    }
  }

  /**
   * Handles the action for clearing the search filter. Resets the search field and reloads all
   * garages.
   */
  @FXML
  private void handleClearFilter() {
    searchField.clear();
    loadGarages();
  }

  /** Handles the action for adding a new garage. Opens the garage form in add mode. */
  @FXML
  public void handleAddGarage() {
    showGarageForm(null);
  }

  /**
   * Handles the action for editing a garage. Opens the garage form in edit mode.
   *
   * @param garage the garage to edit
   */
  private void handleEditGarage(Garage garage) {
    showGarageForm(garage);
  }

  /**
   * Handles the action for deleting a garage. Prompts the user for confirmation and deletes the
   * garage if confirmed.
   *
   * @param garage the garage to delete
   */
  private void handleDeleteGarage(Garage garage) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Delete");
    alert.setHeaderText("Delete Garage");
    alert.setContentText("Are you sure you want to delete garage: " + garage.getGarageName() + "?");

    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.OK) {
                try {
                  if (garageService.delete(garage.getGarageId())) {
                    garageList.remove(garage);
                    showInfo("Garage deleted successfully");
                  }
                } catch (ServiceException e) {
                  showError("Error deleting garage: " + e.getMessage());
                }
              }
            });
  }

  /**
   * Opens the garage form for adding or editing a garage.
   *
   * @param garage the garage to edit, or null for adding a new garage
   */
  private void showGarageForm(Garage garage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/garage-form.fxml"));
      Parent root = loader.load();

      GarageFormController controller = loader.getController();
      controller.setGarage(garage);
      controller.setOnSaveCallback(this::loadGarages);

      Stage stage = new Stage();
      stage.setTitle(garage == null ? "Add New Garage" : "Edit Garage");
      stage.setScene(new Scene(root));
      stage.setResizable(false);
      stage.showAndWait();
    } catch (IOException e) {
      showError("Error opening garage form: " + e.getMessage());
    }
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
