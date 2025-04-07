package com.carservice.service;

import com.carservice.dao.JobDAO;
import com.carservice.model.Job;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing jobs. Provides CRUD operations and additional methods for job-related
 * functionality.
 */
public class JobService implements CrudService<Job, Long> {
  private final JobDAO jobDAO;
  private final ActivityService activityService;

  /** Constructs a JobService with default DAO and ActivityService instances. */
  public JobService() {
    this.jobDAO = new JobDAO();
    this.activityService = new ActivityService();
  }

  /**
   * Finds a job by its ID.
   *
   * @param id the ID of the job.
   * @return the job with the specified ID.
   * @throws ServiceException if the job is not found or an error occurs.
   */
  @Override
  public Job findById(Long id) throws ServiceException {
    try {
      Job job = jobDAO.findById(id);
      if (job == null) {
        throw new ServiceException("Job not found with ID: " + id);
      }
      return job;
    } catch (SQLException e) {
      throw new ServiceException("Error finding job with ID: " + id, e);
    }
  }

  /**
   * Finds jobs associated with a specific car.
   *
   * @param regNo the registration number of the car.
   * @return a list of jobs associated with the car.
   * @throws ServiceException if an error occurs while retrieving jobs.
   */
  public List<Job> findByCar(String regNo) throws ServiceException {
    try {
      return jobDAO.findByCar(regNo);
    } catch (SQLException e) {
      throw new ServiceException("Error finding jobs for car: " + regNo, e);
    }
  }

  /**
   * Finds jobs associated with a specific garage.
   *
   * @param garageId the ID of the garage.
   * @return a list of jobs associated with the garage.
   * @throws ServiceException if an error occurs while retrieving jobs.
   */
  public List<Job> findByGarage(Long garageId) throws ServiceException {
    try {
      return jobDAO.findByGarage(garageId);
    } catch (SQLException e) {
      throw new ServiceException("Error finding jobs for garage: " + garageId, e);
    }
  }

  /**
   * Retrieves all jobs.
   *
   * @return a list of all jobs.
   * @throws ServiceException if an error occurs while retrieving jobs.
   */
  @Override
  public List<Job> findAll() throws ServiceException {
    try {
      return jobDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all jobs", e);
    }
  }

  /**
   * Saves a new job.
   *
   * @param job the job to save.
   * @return the ID of the saved job.
   * @throws ServiceException if validation fails or an error occurs while saving.
   */
  @Override
  public Long save(Job job) throws ServiceException {
    try {
      validateJob(job);
      Long id = jobDAO.save(job);
      activityService.logActivity(
          "JOB", "CREATE", "New job created for car: " + job.getRegNo(), "BougaStefa");
      return id;
    } catch (SQLException e) {
      throw new ServiceException("Error saving job", e);
    }
  }

  /**
   * Updates an existing job.
   *
   * @param job the job to update.
   * @return true if the job was updated successfully, false otherwise.
   * @throws ServiceException if validation fails or an error occurs while updating.
   */
  @Override
  public boolean update(Job job) throws ServiceException {
    try {
      validateJob(job);
      boolean updated = jobDAO.update(job);
      if (updated) {
        activityService.logActivity(
            "JOB", "UPDATE", "Job updated for car: " + job.getRegNo(), "BougaStefa");
      }
      return updated;
    } catch (SQLException e) {
      throw new ServiceException("Error updating job", e);
    }
  }

  /**
   * Deletes a job by its ID.
   *
   * @param jobId the ID of the job to delete.
   * @return true if the job was deleted successfully, false otherwise.
   * @throws ServiceException if an error occurs while deleting the job.
   */
  @Override
  public boolean delete(Long jobId) throws ServiceException {
    try {
      boolean deleted = jobDAO.delete(jobId);
      if (deleted) {
        activityService.logActivity("JOB", "DELETE", "Job deleted with ID: " + jobId, "BougaStefa");
      }
      return deleted;
    } catch (SQLException e) {
      throw new ServiceException("Error deleting job", e);
    }
  }

  /**
   * Calculates the average service cost for a specific customer.
   *
   * @param customerId the ID of the customer.
   * @return the average service cost for the customer.
   * @throws ServiceException if the customer ID is null or an error occurs.
   */
  public Double getAverageServiceCostByCustomer(Long customerId) throws ServiceException {
    try {
      if (customerId == null) {
        throw new ServiceException("Customer ID cannot be null");
      }
      return jobDAO.getAverageServiceCostByCustomer(customerId);
    } catch (SQLException e) {
      throw new ServiceException(
          "Error calculating average service cost for customer ID: " + customerId, e);
    }
  }

  /**
   * Calculates the total number of service days for a specific car.
   *
   * @param regNo the registration number of the car.
   * @return the total number of service days for the car.
   * @throws ServiceException if the registration number is null or an error occurs.
   */
  public long calculateTotalServiceDays(String regNo) throws ServiceException {
    try {
      if (regNo == null || regNo.trim().isEmpty()) {
        throw new ServiceException("Registration number cannot be null or empty");
      }

      List<Job> jobs = findByCar(regNo);
      long totalDays = 0;
      LocalDateTime now = LocalDateTime.now();

      for (Job job : jobs) {
        LocalDateTime outDate = job.getDateOut() != null ? job.getDateOut() : now;
        long days = java.time.Duration.between(job.getDateIn(), outDate).toDays() + 1;
        totalDays += days;
      }

      return totalDays;
    } catch (ServiceException e) {
      throw new ServiceException("Error calculating total service days for car: " + regNo, e);
    }
  }

  /**
   * Validates the job object to ensure it meets the required criteria.
   *
   * @param job the job to validate.
   * @throws ServiceException if validation fails.
   */
  private void validateJob(Job job) throws ServiceException {
    if (job.getDateIn() == null) {
      throw new ServiceException("Job date in cannot be null");
    }
    if (job.getDateOut() != null && job.getDateOut().isBefore(job.getDateIn())) {
      throw new ServiceException("Job date out cannot be before date in");
    }
    if (job.getCost() != null && job.getCost() < 0) {
      throw new ServiceException("Job cost cannot be negative");
    }
    LocalDateTime now = LocalDateTime.now();
    if (job.getDateIn().isAfter(now)) {
      throw new ServiceException("Job date in cannot be in the future");
    }
  }
}
