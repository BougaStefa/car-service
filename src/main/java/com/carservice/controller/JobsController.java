package com.carservice.controller;

import com.carservice.model.Job;
import com.carservice.service.JobService;
import com.carservice.service.PaymentService;
import com.carservice.service.ServiceException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
 * Controller class for managing the Jobs view. Handles user interactions, data loading, and
 * communication with services.
 */
public class JobsController {
  private final JobService jobService;
  private final ObservableList<Job> jobList = FXCollections.observableArrayList();
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  @FXML private TextField searchField;
  @FXML private ComboBox<String> filterType;
  @FXML private Button clearFilterButton;
  @FXML private TableView<Job> jobTable;
  @FXML private TableColumn<Job, Long> idColumn;
  @FXML private TableColumn<Job, Long> garageIdColumn;
  @FXML private TableColumn<Job, String> regNoColumn;
  @FXML private TableColumn<Job, LocalDateTime> dateInColumn;
  @FXML private TableColumn<Job, LocalDateTime> dateOutColumn;
  @FXML private TableColumn<Job, Double> costColumn;
  @FXML private TableColumn<Job, Void> actionsColumn;

  /** Constructor for initializing the JobService dependency. */
  public JobsController() {
    this.jobService = new JobService();
  }

  /** Initializes the controller and sets up the UI components. */
  @FXML
  private void initialize() {
    setupFilterType();
    setupTableColumns();
    setupSearchField();
    loadJobs();
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

  /** Configures the filter type ComboBox with options and sets up a listener for filtering jobs. */
  private void setupFilterType() {
    filterType.setItems(FXCollections.observableArrayList("All", "By Car", "By Garage"));
    filterType.setValue("All");

    filterType.setOnAction(e -> handleSearch());
  }

  /** Configures the table columns with property value factories and custom cell factories. */
  private void setupTableColumns() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));
    garageIdColumn.setCellValueFactory(new PropertyValueFactory<>("garageId"));
    regNoColumn.setCellValueFactory(new PropertyValueFactory<>("regNo"));
    dateInColumn.setCellValueFactory(new PropertyValueFactory<>("dateIn"));
    dateOutColumn.setCellValueFactory(new PropertyValueFactory<>("dateOut"));
    costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));

    // Format date columns to display readable date-time values
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

  /** Configures the actions column with Edit, Delete, and Complete buttons for each row. */
  private void setupActionsColumn() {
    actionsColumn.setCellFactory(
        param ->
            new TableCell<>() {
              private final Button editButton = new Button("Edit");
              private final Button deleteButton = new Button("Delete");
              private final Button completeButton = new Button("Complete");
              private final HBox buttons = new HBox(5, editButton, deleteButton, completeButton);

              {
                buttons.setAlignment(Pos.CENTER);
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                  setGraphic(null);
                  return;
                }

                Job currentJob = getTableView().getItems().get(getIndex());
                if (currentJob == null) {
                  setGraphic(null);
                  return;
                }

                setGraphic(buttons);

                editButton.setOnAction(event -> handleEditJob(currentJob));
                deleteButton.setOnAction(event -> handleDeleteJob(currentJob));
                completeButton.setOnAction(event -> handleCompleteJob(currentJob));

                // Disable complete button if job is already completed
                completeButton.setDisable(currentJob.getDateOut() != null);
              }
            });
  }

  /** Loads the list of jobs and populates the job table. */
  private void loadJobs() {
    try {
      List<Job> jobs = jobService.findAll();
      jobList.setAll(jobs);
      jobTable.setItems(jobList);
    } catch (ServiceException e) {
      showError("Error loading jobs: " + e.getMessage());
    }
  }

  /** Handles the search action by filtering jobs based on the search term and filter type. */
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

  /**
   * Handles the action for clearing the search filter. Resets the search field and filter type,
   * then reloads all jobs.
   */
  @FXML
  private void handleClearFilter() {
    searchField.clear();
    filterType.setValue("All");
    loadJobs();
  }

  /** Handles the action for adding a new job. Opens the job form in add mode. */
  @FXML
  public void handleAddJob() {
    showJobForm(null);
  }

  /**
   * Handles the action for editing a job. Opens the job form in edit mode.
   *
   * @param job the job to edit
   */
  private void handleEditJob(Job job) {
    showJobForm(job);
  }

  /**
   * Handles the action for deleting a job. Prompts the user for confirmation and deletes the job if
   * confirmed.
   *
   * @param job the job to delete
   */
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

  /**
   * Handles the action for completing a job. Prompts the user for confirmation, processes payment,
   * and marks the job as completed.
   *
   * @param job the job to complete
   */
  private void handleCompleteJob(Job job) {
    if (job.getDateOut() != null) {
      showError("Job is already completed");
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Complete Job");
    alert.setHeaderText("Complete Job and Process Payment");
    alert.setContentText("Are you sure you want to complete job ID: " + job.getJobId() + "?");

    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.OK) {
                // Set completion date to now
                job.setDateOut(LocalDateTime.now());

                // Show payment dialog
                Dialog<String> paymentDialog = new Dialog<>();
                paymentDialog.setTitle("Process Payment");
                paymentDialog.setHeaderText("Select Payment Method");

                ButtonType confirmButtonType =
                    new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                paymentDialog
                    .getDialogPane()
                    .getButtonTypes()
                    .addAll(confirmButtonType, ButtonType.CANCEL);

                ComboBox<String> paymentMethodCombo = new ComboBox<>();
                paymentMethodCombo.getItems().addAll("CASH", "CARD", "TRANSFER");
                paymentMethodCombo.setValue("CASH");

                paymentDialog.getDialogPane().setContent(paymentMethodCombo);

                paymentDialog.setResultConverter(
                    dialogButton -> {
                      if (dialogButton == confirmButtonType) {
                        return paymentMethodCombo.getValue();
                      }
                      return null;
                    });

                Optional<String> result = paymentDialog.showAndWait();
                result.ifPresent(
                    paymentMethod -> {
                      try {
                        // Update the job first
                        jobService.update(job);

                        // Process the payment
                        PaymentService paymentService = new PaymentService();
                        paymentService.processJobPayment(job.getJobId(), paymentMethod);

                        // Refresh the table
                        loadJobs();
                        showInfo("Job completed and payment processed successfully");
                      } catch (ServiceException e) {
                        showError("Error processing payment: " + e.getMessage());
                      }
                    });
              }
            });
  }

  /**
   * Opens the job form for adding or editing a job.
   *
   * @param job the job to edit, or null for adding a new job
   */
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
