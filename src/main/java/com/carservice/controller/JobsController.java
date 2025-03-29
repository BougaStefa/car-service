package com.carservice.controller;

import com.carservice.model.Job;
import com.carservice.service.JobService;
import com.carservice.service.ServiceException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

public class JobsController {
  private final JobService jobService;
  private final ObservableList<Job> jobList = FXCollections.observableArrayList();
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  @FXML private TextField searchField;
  @FXML private ComboBox<String> filterType;
  @FXML private TableView<Job> jobTable;
  @FXML private TableColumn<Job, Long> idColumn;
  @FXML private TableColumn<Job, Long> garageIdColumn;
  @FXML private TableColumn<Job, String> regNoColumn;
  @FXML private TableColumn<Job, LocalDateTime> dateInColumn;
  @FXML private TableColumn<Job, LocalDateTime> dateOutColumn;
  @FXML private TableColumn<Job, Double> costColumn;
  @FXML private TableColumn<Job, Void> actionsColumn;

  public JobsController() {
    this.jobService = new JobService();
  }

  @FXML
  private void initialize() {
    setupFilterType();
    setupTableColumns();
    loadJobs();
  }

  private void setupFilterType() {
    filterType.setItems(FXCollections.observableArrayList("All", "By Car", "By Garage"));
    filterType.setValue("All");

    filterType.setOnAction(e -> handleSearch());
  }

  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));
    garageIdColumn.setCellValueFactory(new PropertyValueFactory<>("garageId"));
    regNoColumn.setCellValueFactory(new PropertyValueFactory<>("regNo"));
    dateInColumn.setCellValueFactory(new PropertyValueFactory<>("dateIn"));
    dateOutColumn.setCellValueFactory(new PropertyValueFactory<>("dateOut"));
    costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));

    // Format date columns
    dateInColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  setText(DATE_FORMATTER.format(item));
                }
              }
            });

    dateOutColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  setText(DATE_FORMATTER.format(item));
                }
              }
            });

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
                    event -> handleEditJob(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(
                    event -> handleDeleteJob(getTableView().getItems().get(getIndex())));
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
              }
            });
  }

  private void loadJobs() {
    try {
      List<Job> jobs = jobService.findAll();
      jobList.setAll(jobs);
      jobTable.setItems(jobList);
    } catch (ServiceException e) {
      showError("Error loading jobs: " + e.getMessage());
    }
  }

  @FXML
  private void handleSearch() {
    String searchTerm = searchField.getText().trim();
    String filter = filterType.getValue();

    try {
      List<Job> jobs;
      if (searchTerm.isEmpty() || filter.equals("All")) {
        loadJobs();
        return;
      }

      if (filter.equals("By Car")) {
        jobs = jobService.findByCar(searchTerm);
      } else { // By Garage
        Long garageId;
        try {
          garageId = Long.parseLong(searchTerm);
          jobs = jobService.findByGarage(garageId);
        } catch (NumberFormatException e) {
          showError("Please enter a valid garage ID");
          return;
        }
      }
      jobList.setAll(jobs);
    } catch (ServiceException e) {
      showError("Error searching jobs: " + e.getMessage());
    }
  }

  @FXML
  public void handleAddJob() {
    showJobForm(null);
  }

  private void handleEditJob(Job job) {
    showJobForm(job);
  }

  private void handleDeleteJob(Job job) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Delete");
    alert.setHeaderText("Delete Job");
    alert.setContentText("Are you sure you want to delete job ID: " + job.getJobId() + "?");

    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.OK) {
                try {
                  if (jobService.delete(job.getJobId())) {
                    jobList.remove(job);
                    showInfo("Job deleted successfully");
                  }
                } catch (ServiceException e) {
                  showError("Error deleting job: " + e.getMessage());
                }
              }
            });
  }

  private void showJobForm(Job job) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/job-form.fxml"));
      Parent root = loader.load();

      JobFormController controller = loader.getController();
      controller.setJob(job);
      controller.setOnSaveCallback(this::loadJobs);

      Stage stage = new Stage();
      stage.setTitle(job == null ? "Add New Job" : "Edit Job");
      stage.setScene(new Scene(root));
      stage.setResizable(false);
      stage.showAndWait();
    } catch (IOException e) {
      showError("Error opening job form: " + e.getMessage());
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
