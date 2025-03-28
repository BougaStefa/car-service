package com.carservice.controller;

import com.carservice.model.Job;
import com.carservice.service.JobService;
import com.carservice.service.ServiceException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class JobFormController {
  @FXML private TextField garageIdField;
  @FXML private TextField regNoField;
  @FXML private DatePicker dateInPicker;
  @FXML private DatePicker dateOutPicker;
  @FXML private TextField timeInField;
  @FXML private TextField timeOutField;
  @FXML private TextField costField;

  private JobService jobService;
  private Job job;
  private boolean isEditMode;
  private Runnable onSaveCallback;

  @FXML
  public void initialize() {
    jobService = new JobService();
  }

  public void setJob(Job job) {
    this.job = job;
    this.isEditMode = job != null;

    if (isEditMode) {
      garageIdField.setText(String.valueOf(job.getGarageId()));
      regNoField.setText(job.getRegNo());

      // Set date in
      dateInPicker.setValue(job.getDateIn().toLocalDate());
      timeInField.setText(
          String.format("%02d:%02d", job.getDateIn().getHour(), job.getDateIn().getMinute()));

      // Set date out if exists
      if (job.getDateOut() != null) {
        dateOutPicker.setValue(job.getDateOut().toLocalDate());
        timeOutField.setText(
            String.format("%02d:%02d", job.getDateOut().getHour(), job.getDateOut().getMinute()));
      }

      costField.setText(String.valueOf(job.getCost()));
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
        updateJob();
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

  private void createJob() throws ServiceException {
    Job newJob = new Job();
    populateJobData(newJob);
    jobService.save(newJob);
  }

  private void updateJob() throws ServiceException {
    populateJobData(job);
    jobService.update(job);
  }

  private void populateJobData(Job job) throws ServiceException {
    try {
      job.setGarageId(Long.parseLong(garageIdField.getText().trim()));
      job.setRegNo(regNoField.getText().trim());

      // Set date in
      LocalDateTime dateIn = combineDateTime(dateInPicker.getValue(), timeInField.getText());
      job.setDateIn(dateIn);

      // Set date out if provided
      if (dateOutPicker.getValue() != null && !timeOutField.getText().isEmpty()) {
        LocalDateTime dateOut = combineDateTime(dateOutPicker.getValue(), timeOutField.getText());
        job.setDateOut(dateOut);
      }

      // Set cost
      String costText = costField.getText().trim();
      if (!costText.isEmpty()) {
        job.setCost(Double.parseDouble(costText));
      }
    } catch (NumberFormatException e) {
      throw new ServiceException("Invalid number format: " + e.getMessage());
    }
  }

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

  @FXML
  private void handleCancel() {
    closeForm();
  }

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

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void closeForm() {
    ((Stage) garageIdField.getScene().getWindow()).close();
  }
}
