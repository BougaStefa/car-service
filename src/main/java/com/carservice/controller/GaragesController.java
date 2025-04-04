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

  public GaragesController() {
    this.garageService = new GarageService();
  }

  @FXML
  private void initialize() {
    setupTableColumns();
    setupSearchField();
    loadGarages();
  }

  private void setupSearchField() {
    searchField.setOnKeyPressed(
        event -> {
          if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            handleSearch();
          }
        });
  }

  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("garageId"));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("garageName"));
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    townColumn.setCellValueFactory(new PropertyValueFactory<>("town"));
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

  private void loadGarages() {
    try {
      List<Garage> garages = garageService.findAll();
      garageList.setAll(garages);
      garageTable.setItems(garageList);
    } catch (ServiceException e) {
      showError("Error loading garages: " + e.getMessage());
    }
  }

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

  @FXML
  private void handleClearFilter() {
    searchField.clear();
    loadGarages();
  }

  @FXML
  public void handleAddGarage() {
    showGarageForm(null);
  }

  private void handleEditGarage(Garage garage) {
    showGarageForm(garage);
  }

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
