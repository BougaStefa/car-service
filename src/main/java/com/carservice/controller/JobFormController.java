package com.carservice.controller;

import com.carservice.model.Job;
import com.carservice.service.JobService;
import com.carservice.service.PaymentService;
import com.carservice.service.ServiceException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller class for managing the Job Form view. Handles user interactions, form validation, and
 * communication with the JobService and PaymentService.
 */
public class JobFormController {
  @FXML private TextField garageIdField;
  @FXML private TextField regNoField;
  @FXML private DatePicker dateInPicker;
  @FXML private DatePicker dateOutPicker;
  @FXML private TextField timeInField;
  @FXML private TextField timeOutField;
  @FXML private TextField costField;

  private JobService jobService;
  private PaymentService paymentService = new PaymentService();
  private Job job;
  private boolean isEditMode;
  private Runnable onSaveCallback;

  /** Initializes the controller and sets up the JobService instance. */
  @FXML
  public void initialize() {
    jobService = new JobService();
  }

  /**
   * Sets the job to be edited or creates a new job if null. Populates the form fields with the
   * job's data if in edit mode.
   *
   * @param job the job to edit or null for a new job
   */
  public void setJob(Job job) {
    this.job = job;
    this.isEditMode = job != null;

    if (isEditMode) {
      garageIdField.setText(String.valueOf(job.getGarageId()));
      regNoField.setText(job.getRegNo());

      // Populate date and time for job start
      dateInPicker.setValue(job.getDateIn().toLocalDate());
      timeInField.setText(
          String.format("%02d:%02d", job.getDateIn().getHour(), job.getDateIn().getMinute()));

      // Populate date and time for job end if available
      if (job.getDateOut() != null) {
        dateOutPicker.setValue(job.getDateOut().toLocalDate());
        timeOutField.setText(
            String.format("%02d:%02d", job.getDateOut().getHour(), job.getDateOut().getMinute()));
      }

      costField.setText(String.valueOf(job.getCost()));
    }
  }

  /**
   * Sets a callback to be executed after saving the job.
   *
   * @param callback the callback to execute
   */
  public void setOnSaveCallback(Runnable callback) {
    this.onSaveCallback = callback;
  }

  /**
   * Handles the save button action. Validates input, saves or updates the job, processes payment if
   * applicable, and closes the form.
   */
  @FXML
  private void handleSave() {
    if (!validateInput()) {
      return;
    }

    try {
      if (isEditMode) {
        updateJob();
        handlePayment(job);
      } else {
        createJob();
      }

      if (onSaveCallback != null) {
        onSaveCallback.run();
      }

      closeForm();
    } catch (ServiceException e) {
      showError("Error saving job: " + e.getMessage());
    }
  }

  /**
   * Creates a new job and saves it using the JobService.
   *
   * @throws ServiceException if an error occurs while saving the job
   */
  private void createJob() throws ServiceException {
    Job newJob = new Job();
    populateJobData(newJob);
    jobService.save(newJob);
  }

  /**
   * Updates the existing job using the JobService.
   *
   * @throws ServiceException if an error occurs while updating the job
   */
  private void updateJob() throws ServiceException {
    populateJobData(job);
    jobService.update(job);
  }

  /**
   * Populates the job object with data from the form fields.
   *
   * @param job the job object to populate
   * @throws ServiceException if invalid data is provided
   */
  private void populateJobData(Job job) throws ServiceException {
    try {
      job.setGarageId(Long.parseLong(garageIdField.getText().trim()));
      job.setRegNo(regNoField.getText().trim());

      // Combine date and time for job start
      LocalDateTime dateIn = combineDateTime(dateInPicker.getValue(), timeInField.getText());
      job.setDateIn(dateIn);

      // Combine date and time for job end if provided
      if (dateOutPicker.getValue() != null && !timeOutField.getText().isEmpty()) {
        LocalDateTime dateOut = combineDateTime(dateOutPicker.getValue(), timeOutField.getText());
        job.setDateOut(dateOut);
      }

      // Parse and set cost if provided
      String costText = costField.getText().trim();
      if (!costText.isEmpty()) {
        job.setCost(Double.parseDouble(costText));
      }
    } catch (NumberFormatException e) {
      throw new ServiceException("Invalid number format: " + e.getMessage());
    }
  }

  /**
   * Combines a date and time string into a LocalDateTime object.
   *
   * @param date the date
   * @param timeStr the time string in HH:mm format
   * @return the combined LocalDateTime object
   * @throws ServiceException if the time format is invalid
   */
  private LocalDateTime combineDateTime(LocalDate date, String timeStr) throws ServiceException {
    try {
      String[] timeParts = timeStr.split(":");
      if (timeParts.length != 2) {
        throw new ServiceException("Invalid time format. Use HH:mm");
      }

      int hour = Integer.parseInt(timeParts[0]);
      int minute = Integer.parseInt(timeParts[1]);

      if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
        throw new ServiceException("Invalid time values");
      }

      return LocalDateTime.of(date, LocalTime.of(hour, minute));
    } catch (NumberFormatException e) {
      throw new ServiceException("Invalid time format: " + e.getMessage());
    }
  }

  /** Handles the cancel button action and closes the form. */
  @FXML
  private void handleCancel() {
    closeForm();
  }

  /**
   * Validates the input fields and displays error messages if invalid.
   *
   * @return true if the input is valid, false otherwise
   */
  private boolean validateInput() {
    StringBuilder errors = new StringBuilder();

    if (garageIdField.getText().trim().isEmpty()) {
      errors.append("Garage ID is required\n");
    }

    if (regNoField.getText().trim().isEmpty()) {
      errors.append("Registration number is required\n");
    }

    if (dateInPicker.getValue() == null) {
      errors.append("Date in is required\n");
    }

    if (timeInField.getText().trim().isEmpty()) {
      errors.append("Time in is required\n");
    }

    // Validate cost if provided
    String costText = costField.getText().trim();
    if (!costText.isEmpty()) {
      try {
        double cost = Double.parseDouble(costText);
        if (cost < 0) {
          errors.append("Cost cannot be negative\n");
        }
      } catch (NumberFormatException e) {
        errors.append("Invalid cost format\n");
      }
    }

    // Validate that date out is after date in if both are provided
    if (dateOutPicker.getValue() != null && !timeOutField.getText().isEmpty()) {
      try {
        LocalDateTime dateIn = combineDateTime(dateInPicker.getValue(), timeInField.getText());
        LocalDateTime dateOut = combineDateTime(dateOutPicker.getValue(), timeOutField.getText());

        if (dateOut.isBefore(dateIn)) {
          errors.append("Date out must be after date in\n");
        }
      } catch (ServiceException e) {
        errors.append(e.getMessage()).append("\n");
      }
    }

    if (errors.length() > 0) {
      showError("Please correct the following errors:\n" + errors.toString());
      return false;
    }

    return true;
  }

  /**
   * Handles payment processing for the job if it has a completion date.
   *
   * @param job the job for which payment is to be processed
   * @throws ServiceException if an error occurs during payment processing
   */
  private void handlePayment(Job job) throws ServiceException {
    if (job.getDateOut() != null) {
      // Show payment dialog
      Dialog<String> dialog = new Dialog<>();
      dialog.setTitle("Process Payment");
      dialog.setHeaderText("Select Payment Method");

      ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
      dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

      ComboBox<String> paymentMethodCombo = new ComboBox<>();
      paymentMethodCombo.getItems().addAll("CASH", "CARD", "TRANSFER");
      paymentMethodCombo.setValue("CASH");

      dialog.getDialogPane().setContent(paymentMethodCombo);

      dialog.setResultConverter(
          dialogButton -> {
            if (dialogButton == confirmButtonType) {
              return paymentMethodCombo.getValue();
            }
            return null;
          });

      Optional<String> result = dialog.showAndWait();
      result.ifPresent(
          paymentMethod -> {
            try {
              paymentService.processJobPayment(job.getJobId(), paymentMethod);
              showInfo("Payment processed successfully");
            } catch (ServiceException e) {
              showError("Error processing payment: " + e.getMessage());
            }
          });
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

  /** Closes the form by closing the current stage. */
  private void closeForm() {
    ((Stage) garageIdField.getScene().getWindow()).close();
  }
}
